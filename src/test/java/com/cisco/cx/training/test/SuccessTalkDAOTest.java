package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.dao.impl.SuccessTalkDAOImpl;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkSession;

@RunWith(SpringRunner.class)
public class SuccessTalkDAOTest {

	@Mock
	private ElasticSearchDAO elasticSearchDAO;

	@Mock
	private PropertyConfiguration config;

	@InjectMocks
	private SuccessTalkDAO successTalkDAO = new SuccessTalkDAOImpl();

	@Test
	public void insertSuccessTalk() throws IOException {
		SuccessTalk successTalk = getSuccessTask();
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.saveEntry(config.getSuccessTalkIndex(), successTalk, SuccessTalk.class)).thenReturn(successTalk);
		successTalkDAO.insertSuccessTalk(successTalk);
	}
	
	@Test(expected = GenericException.class)
	public void insertSuccessTalkESFailure() throws IOException {
		SuccessTalk successTalk = getSuccessTask();
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.saveEntry(config.getSuccessTalkIndex(), successTalk, SuccessTalk.class)).thenThrow(IOException.class);
		successTalkDAO.insertSuccessTalk(successTalk);
	}

	@Test
	public void getAllSuccessTalks() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		SuccessTalk successTalk = getSuccessTask();
		ElasticSearchResults<SuccessTalk> results = new ElasticSearchResults<>();
		results.addDocument(successTalk);
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class)).thenReturn(results);
		successTalkDAO.getAllSuccessTalks();
	}
	
	@Test(expected = GenericException.class)
	public void getAllSuccessTalksESFailure() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		SuccessTalk successTalk = getSuccessTask();
		ElasticSearchResults<SuccessTalk> results = new ElasticSearchResults<>();
		results.addDocument(successTalk);
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class)).thenThrow(IOException.class);
		successTalkDAO.getAllSuccessTalks();
	}

	@Test
	public void getFilteredSuccessTalks() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", "IBN");
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", "usecase");
		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		ElasticSearchResults<SuccessTalk> results = new ElasticSearchResults<>();
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class)).thenReturn(results);
		successTalkDAO.getFilteredSuccessTalks("IBN", "usecase");
	}
	
	@Test(expected = GenericException.class)
	public void getFilteredSuccessTalksESFailure() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder matchQueryBuilderSolution = QueryBuilders.matchPhraseQuery("solution.keyword", "IBN");
		QueryBuilder matchQueryBuilderTechnology = QueryBuilders.matchPhraseQuery("usecase.keyword", "usecase");
		boolQuery = boolQuery.must(matchQueryBuilderSolution).must(matchQueryBuilderTechnology);
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class)).thenThrow(IOException.class);
		successTalkDAO.getFilteredSuccessTalks("IBN", "usecase");
	}

	@Test
	public void registerUser() throws IOException {
		SuccessTalk successTalk = getSuccessTask();
		when(config.getSuccessTalkIndex()).thenReturn("");
		String successTalkId = "successTalkId";
		String successTalkSessionId = "successTalkSessionId";
		when(elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class)).thenReturn(successTalk);
		successTalkDAO.registerUser(successTalkSessionId, successTalkId);
	}
	
	@Test(expected = GenericException.class)
	public void registerUserESFailure() throws IOException {
		when(config.getSuccessTalkIndex()).thenReturn("");
		String successTalkId = "successTalkId";
		String successTalkSessionId = "successTalkSessionId";
		when(elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class)).thenThrow(GenericException.class);
		successTalkDAO.registerUser(successTalkSessionId, successTalkId);
	}

	@Test
	public void cancelRegistration() throws IOException {
		when(config.getSuccessTalkIndex()).thenReturn("");
		String successTalkId = "successTalkId";
		String successTalkSessionId = "successTalkSessionId";
		when(elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class)).thenReturn(getSuccessTask());
		successTalkDAO.cancelRegistration(successTalkSessionId, successTalkId);
	}
	
	@Test(expected = GenericException.class)
	public void cancelRegistrationESFailure() throws IOException {
		when(config.getSuccessTalkIndex()).thenReturn("");
		String successTalkId = "successTalkId";
		String successTalkSessionId = "successTalkSessionId";
		when(elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class)).thenThrow(GenericException.class);
		successTalkDAO.cancelRegistration(successTalkSessionId, successTalkId);
	}

	private SuccessTalk getSuccessTask() {
		SuccessTalk successTalk = new SuccessTalk();
		successTalk.setBookmark(true);
		successTalk.setDescription("");
		successTalk.setDocId("id");
		successTalk.setDuration(10L);
		successTalk.setImageUrl("");
		successTalk.setRecordingUrl("");
		List<SuccessTalkSession> sessions = new ArrayList<>();
		SuccessTalkSession session = new SuccessTalkSession();
		session.setDocId("");
		session.setPresenterName("John Doe");
		session.setRegion("region");
		session.setRegistrationUrl("");
		session.setScheduled(true);
		session.setSessionId("");
		session.setSessionStartDate("");
		Arrays.asList(session);
		successTalk.setSessions(sessions);
		return successTalk;
	}
}