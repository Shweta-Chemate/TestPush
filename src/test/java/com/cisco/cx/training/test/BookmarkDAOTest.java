package com.cisco.cx.training.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

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
import com.cisco.cx.training.app.exception.GenericException;
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
	
	@Test(expected = GenericException.class)
	public void getBookmarksError() throws IOException {
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
		when(elasticSearchDAO.query(config.getBookmarksIndex(), sourceBuilder, BookmarkResponseSchema.class)).thenThrow(IOException.class);
		bookmarkDAO.getBookmarks(email, entityId);

	}

	@Test(expected = GenericException.class)
	public void createOrUpdateBookmarkWithError() throws IOException {
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
		when(elasticSearchDAO.query(config.getBookmarksIndex(), sourceBuilder, BookmarkResponseSchema.class))
				.thenThrow(IOException.class);

		BookmarkResponseSchema bookmark = new BookmarkResponseSchema();
		bookmark.setBookmark(true);
		bookmark.setBookmarkRequestId("bookmarkRequestId");
		bookmark.setCreated(1L);
		bookmark.setDocId("docid");
		bookmark.setEmail("email");
		bookmark.setId("id");
		bookmark.setTitle("title");
		bookmark.setUpdated(1L);

		when(elasticSearchDAO.saveEntry(config.getBookmarksIndex(), bookmark, BookmarkResponseSchema.class))
				.thenThrow(IOException.class);
		BookmarkResponseSchema actual = bookmarkDAO.createOrUpdate(bookmark);
		assertEquals(bookmark, actual);
	}

	@Test
	public void createOrUpdateBookmarkTest() throws IOException {
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
		when(elasticSearchDAO.query(config.getBookmarksIndex(), sourceBuilder, BookmarkResponseSchema.class))
				.thenReturn(results);

		BookmarkResponseSchema bookmark = new BookmarkResponseSchema();
		bookmark.setBookmark(true);
		bookmark.setBookmarkRequestId("bookmarkRequestId");
		bookmark.setCreated(1L);
		bookmark.setDocId("docid");
		bookmark.setEmail("email");
		bookmark.setId("id");
		bookmark.setTitle("title");
		bookmark.setUpdated(1L);

		when(elasticSearchDAO.saveEntry(config.getBookmarksIndex(), bookmark, BookmarkResponseSchema.class))
				.thenReturn(bookmark);
		BookmarkResponseSchema actual = bookmarkDAO.createOrUpdate(bookmark);
		assertEquals(bookmark, actual);
	}

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