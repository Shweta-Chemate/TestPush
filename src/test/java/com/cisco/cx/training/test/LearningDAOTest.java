package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.LearningDAO;
import com.cisco.cx.training.app.dao.impl.LearningDAOImpl;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.LearningModel;

@RunWith(SpringRunner.class)
public class LearningDAOTest {
	@Mock
	private ElasticSearchDAO elasticSearchDAO;

	@Mock
	private PropertyConfiguration config;

	@InjectMocks
	private LearningDAO learningDAO = new LearningDAOImpl();

	private final String INDEX = "cxpp_success_academy_alias";

	@Test
	public void insertLearning() throws IOException {
		Learning learning = learning();
		when(elasticSearchDAO.saveEntry(INDEX, learning, Learning.class)).thenReturn(learning);
		learningDAO.insertLearning(learning);
	}

	@Test(expected = GenericException.class)
	public void insertLearningESFailure() throws IOException {
		Learning learning = learning();
		when(elasticSearchDAO.saveEntry(INDEX, learning, Learning.class)).thenThrow(IOException.class);
		learningDAO.insertLearning(learning);
	}

	@Test
	public void getLearnings() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		ElasticSearchResults<Learning> results = new ElasticSearchResults<Learning>();
		results.addDocument(learning());
		when(elasticSearchDAO.query(INDEX, sourceBuilder, Learning.class)).thenReturn(results);
		learningDAO.getLearnings();
	}

	@Test(expected = GenericException.class)
	public void getLearningsESFailure() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		when(elasticSearchDAO.query(INDEX, sourceBuilder, Learning.class)).thenThrow(IOException.class);
		learningDAO.getLearnings();
	}

	@Test
	public void getFilteredLearnings() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", "solution");
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", "usecase");
		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		ElasticSearchResults<Learning> results = new ElasticSearchResults<Learning>();
		results.addDocument(learning());
		when(elasticSearchDAO.query(INDEX, sourceBuilder, Learning.class)).thenReturn(results);
		learningDAO.getFilteredLearnings("solution", "usecase");
	}
	
	@Test(expected = GenericException.class)
	public void getFilteredLearningsESFailure() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", "solution");
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", "usecase");
		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		when(elasticSearchDAO.query(INDEX, sourceBuilder, Learning.class)).thenThrow(IOException.class);
		learningDAO.getFilteredLearnings("solution", "usecase");
	}

	private Learning learning() {
		Learning learning = new Learning();
		learning.setAlFrescoId("alFrescoId");
		learning.setCategory("category");
		learning.setDescription("description");
		learning.setDocId("docId");
		learning.setImg("img");
		learning.setIsCentralTracked("isCentralTracked");
		learning.setName("name");
		learning.setNotes("notes");
		learning.setSolution("solution");
		learning.setStar(5);
		learning.setStatus("status");
		learning.setTimeCompleted(1L);
		learning.setTimeDuration(1L);
		learning.setUrl("url");
		learning.setUsecase("usecase");
		return learning;
	}
}
