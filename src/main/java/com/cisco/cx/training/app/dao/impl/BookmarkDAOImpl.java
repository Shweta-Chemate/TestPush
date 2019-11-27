package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.ElasticSearchResults;

@Repository
public class BookmarkDAOImpl implements BookmarkDAO {
	private static final Logger LOG = LoggerFactory.getLogger(BookmarkDAOImpl.class);

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;

	@Autowired
	private PropertyConfiguration config;
	
	private static final String ERROR_MESSAGE = "Error while invoking ES API";

	@Override
	public BookmarkResponseSchema createOrUpdate(BookmarkResponseSchema bookmarkResponseSchema) {
		BookmarkResponseSchema savedBookMark;

		List<BookmarkResponseSchema> bookmarks = getBookmarks(bookmarkResponseSchema.getEmail(), bookmarkResponseSchema.getId());

		try {
			// Check if document already exists. If yes update else create
			if (bookmarks.isEmpty()) {
				// create
				bookmarkResponseSchema.setCreated(System.currentTimeMillis());
			} else {
				// update
				LOG.info("Records found, Updating");
				bookmarkResponseSchema.setBookmarkRequestId(bookmarks.get(0).getDocId());
				bookmarkResponseSchema.setUpdated(System.currentTimeMillis());
			}
			savedBookMark = elasticSearchDAO.saveEntry(config.getBookmarksIndex(), bookmarkResponseSchema,
					BookmarkResponseSchema.class);
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}

		return savedBookMark;
	}

	@Override
	public List<BookmarkResponseSchema> getBookmarks(String email, String entityId) {
		List<BookmarkResponseSchema> searchHits = new ArrayList<>();

		try {
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			BoolQueryBuilder boolQuery = new BoolQueryBuilder();
			QueryBuilder ccoIdQuery = QueryBuilders.matchPhraseQuery("email.keyword", email);
			QueryBuilder entityIdQuery;
			boolQuery.must(ccoIdQuery);

			if (StringUtils.isNotBlank(entityId)) {
				entityIdQuery = QueryBuilders.matchPhraseQuery("id.keyword", entityId);
				boolQuery.must(entityIdQuery);
			}

			sourceBuilder.query(boolQuery);
			sourceBuilder.size(1000);

			ElasticSearchResults<BookmarkResponseSchema> results = elasticSearchDAO.query(config.getBookmarksIndex(),
					sourceBuilder, BookmarkResponseSchema.class);
			if (results != null) {
				searchHits = results.getDocuments();
			}
			return searchHits;
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		} catch (Exception e) {
			LOG.error(ERROR_MESSAGE, e);
			throw new GenericException(ERROR_MESSAGE);
		}
	}
}
