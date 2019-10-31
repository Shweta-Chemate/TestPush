package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.impl.BookmarkDAOImpl;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.ElasticSearchResults;

@RunWith(SpringRunner.class)
public class BookmarkDAOTest {
	@Mock
	private ElasticSearchDAO elasticSearchDAO;

	@Mock
	private PropertyConfiguration config;

	@InjectMocks
	private BookmarkDAO bookmarkDAO = new BookmarkDAOImpl();

	@Test
	public void getBookmarks() throws IOException {
		String entityId = "entityId";
		String email = "email";
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder ccoIdQuery = QueryBuilders.matchPhraseQuery("email.keyword", email);
		QueryBuilder entityIdQuery;
		boolQuery.must(ccoIdQuery);
		entityIdQuery = QueryBuilders.matchPhraseQuery("id.keyword", entityId);
		boolQuery.must(entityIdQuery);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(1000);
		when(config.getBookmarksIndex()).thenReturn("");
		ElasticSearchResults<BookmarkResponseSchema> results = new ElasticSearchResults<>();
		results.addDocument(getBookmarkResponseSchema());
		when(elasticSearchDAO.query(config.getBookmarksIndex(), sourceBuilder, BookmarkResponseSchema.class)).thenReturn(results);
		bookmarkDAO.getBookmarks(email, entityId);

	}
	
//	@Test
//	public void createOrUpdateBookmark() throws IOException {
//		String entityId = "entityId";
//		String email = "email";
//		when(config.getBookmarksIndex()).thenReturn("");
//		ElasticSearchResults<BookmarkResponseSchema> results = new ElasticSearchResults<>();
//		results.addDocument(getBookmarkResponseSchema());
//		List<BookmarkResponseSchema> asList = ()Arrays.asList(getBookmarkResponseSchema());
//		when(bookmarkDAO.getBookmarks(email, entityId)).thenReturn(Arrays.asList(getBookmarkResponseSchema()));
//	}

	private BookmarkResponseSchema getBookmarkResponseSchema() {
		BookmarkResponseSchema schema = new BookmarkResponseSchema();
		schema.setBookmark(true);
		schema.setBookmarkRequestId("bookmarkRequestId");
		schema.setCreated(1L);
		schema.setDocId("docid");
		schema.setEmail("email");
		schema.setId("id");
		schema.setTitle("title");
		schema.setUpdated(1L);
		return schema;
	}

}