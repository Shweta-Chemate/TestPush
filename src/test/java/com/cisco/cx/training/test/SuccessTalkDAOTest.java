package com.cisco.cx.training.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.dao.impl.SuccessTalkDAOImpl;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;

@ExtendWith(SpringExtension.class)
public class SuccessTalkDAOTest {

	@Mock
	private ElasticSearchDAO elasticSearchDAO;
	
	@Mock
    private PropertyConfiguration config;

	@Mock
	private BookmarkDAO bookmarkDAO;

	@InjectMocks
	private SuccessTalkDAO successTalkDAO = new SuccessTalkDAOImpl();

	private Long eventTime ;
	
	@BeforeEach
	public void setUp() {
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.MONTH, 1);
		eventTime = c.getTime().getTime();
	}
	
	@Test
	public void insertSuccessTalk() throws IOException {
		SuccessTalk successTalk = getSuccessTask();
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.saveEntry(config.getSuccessTalkIndex(), successTalk, SuccessTalk.class)).thenReturn(successTalk);
		successTalkDAO.insertSuccessTalk(successTalk);
	}
	
	@Test
	public void insertSuccessTalkESFailure() throws IOException {
		SuccessTalk successTalk = getSuccessTask();
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.saveEntry(config.getSuccessTalkIndex(), successTalk, SuccessTalk.class)).thenThrow(IOException.class);
		assertThrows(GenericException.class, () -> {
			successTalkDAO.insertSuccessTalk(successTalk);
		});
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
	
	@Test
	public void findSuccessTalks() throws IOException {
		String title = "title";
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchPhraseQuery("title", title));
        sourceBuilder.size(1);
        SuccessTalk successTalk = getSuccessTask();
		ElasticSearchResults<SuccessTalk> results = new ElasticSearchResults<>();
		results.addDocument(successTalk);
		when(config.getSuccessTalkIndex()).thenReturn("");
		when(elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class)).thenReturn(results);
		successTalkDAO.findSuccessTalk(title, successTalk.getSessions().get(0).getSessionStartDate());
	}
	
	@Test
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
		assertThrows(GenericException.class, () -> {
			successTalkDAO.getAllSuccessTalks();
		});
		
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
	
	@Test
	public void registerUserESFailure() throws IOException {
		when(config.getSuccessTalkIndex()).thenReturn("");
		String successTalkId = "successTalkId";
		String successTalkSessionId = "successTalkSessionId";
		when(elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class)).thenThrow(IOException.class);
		assertThrows(GenericException.class, () -> {
			successTalkDAO.registerUser(successTalkSessionId, successTalkId);
		});
	}

	@Test
	public void cancelRegistration() throws IOException {
		when(config.getSuccessTalkIndex()).thenReturn("");
		String successTalkId = "successTalkId";
		String successTalkSessionId = "successTalkSessionId";
		when(elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class)).thenReturn(getSuccessTask());
		successTalkDAO.cancelRegistration(successTalkSessionId, successTalkId);
	}
	
	@Test
	public void cancelRegistrationESFailure() throws IOException {
		when(config.getSuccessTalkIndex()).thenReturn("");
		String successTalkId = "successTalkId";
		String successTalkSessionId = "successTalkSessionId";
		when(elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class)).thenThrow(IOException.class);
		assertThrows(GenericException.class, () -> {
			successTalkDAO.cancelRegistration(successTalkSessionId, successTalkId);
		});
	}
	
	@Test
	public void getUserSuccessTalks() throws IOException {
		String email = "email";
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		SuccessTalk successTalk = getSuccessTask();
		ElasticSearchResults<SuccessTalk> results = new ElasticSearchResults<>();
		results.addDocument(successTalk);
		when(elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class)).thenReturn(results);
		
        SearchSourceBuilder registrationSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder registrationBoolQuery = new BoolQueryBuilder();
        QueryBuilder emailQuery = QueryBuilders.matchPhraseQuery("email.keyword", email);
        QueryBuilder transactionType = QueryBuilders.matchPhraseQuery("registrationStatus.keyword", SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
        registrationBoolQuery.must(emailQuery).must(transactionType);
        registrationSourceBuilder.query(registrationBoolQuery);
        registrationSourceBuilder.size(1000);
		when(config.getSuccessTalkUserRegistrationsIndex()).thenReturn("");
		List<SuccesstalkUserRegEsSchema> registeredSuccessTalkList = new ArrayList<SuccesstalkUserRegEsSchema>();
		registeredSuccessTalkList.add(getRegistration());
		ElasticSearchResults<SuccesstalkUserRegEsSchema> registrationResults = new ElasticSearchResults<>();
		registrationResults.addDocument(getRegistration());
		when(elasticSearchDAO.query(config.getSuccessTalkUserRegistrationsIndex(), registrationSourceBuilder, SuccesstalkUserRegEsSchema.class))
				.thenReturn(registrationResults);
		
		BookmarkResponseSchema bookmark = new BookmarkResponseSchema();
		bookmark.setBookmark(true);
		bookmark.setBookmarkRequestId("bookmarkRequestId");
		bookmark.setCreated(1L);
		bookmark.setDocId("docid");
		bookmark.setCcoid("ccoid");
		bookmark.setId("id");
		bookmark.setTitle("title");
		bookmark.setUpdated(1L);
		List<BookmarkResponseSchema> bookMarkList= new ArrayList<BookmarkResponseSchema>();
		bookMarkList.add(bookmark);
		when(bookmarkDAO.getBookmarks(email, null)).thenReturn(bookMarkList);
		successTalkDAO.getUserSuccessTalks(email);
	}
	
	@Test
	public void getUserSuccessTalksError() throws IOException {
		String email = "email";
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);
		SuccessTalk successTalk = getSuccessTask();
		ElasticSearchResults<SuccessTalk> results = new ElasticSearchResults<SuccessTalk>();
		results.addDocument(successTalk);
		when(elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class)).thenThrow(IOException.class);
		
        SearchSourceBuilder registrationSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder registrationBoolQuery = new BoolQueryBuilder();
        QueryBuilder emailQuery = QueryBuilders.matchPhraseQuery("email.keyword", email);
        QueryBuilder transactionType = QueryBuilders.matchPhraseQuery("registrationStatus.keyword", SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
        registrationBoolQuery.must(emailQuery).must(transactionType);
        registrationSourceBuilder.query(registrationBoolQuery);
        registrationSourceBuilder.size(1000);
		when(config.getSuccessTalkUserRegistrationsIndex()).thenReturn("");
		List<SuccesstalkUserRegEsSchema> registeredSuccessTalkList = new ArrayList<SuccesstalkUserRegEsSchema>();
		registeredSuccessTalkList.add(getRegistration());
		ElasticSearchResults<SuccesstalkUserRegEsSchema> registrationResults = new ElasticSearchResults<>();
		registrationResults.addDocument(getRegistration());
		when(elasticSearchDAO.query(config.getSuccessTalkUserRegistrationsIndex(), registrationSourceBuilder, SuccesstalkUserRegEsSchema.class))
		.thenThrow(IOException.class);
		
		BookmarkResponseSchema bookmark = new BookmarkResponseSchema();
		bookmark.setBookmark(true);
		bookmark.setBookmarkRequestId("bookmarkRequestId");
		bookmark.setCreated(eventTime);
		bookmark.setDocId("docid");
		bookmark.setCcoid("ccoid");
		bookmark.setId("id");
		bookmark.setTitle("title");
		bookmark.setUpdated(eventTime);
		List<BookmarkResponseSchema> bookMarkList= new ArrayList<BookmarkResponseSchema>();
		bookMarkList.add(bookmark);
		when(bookmarkDAO.getBookmarks(email, null)).thenReturn(bookMarkList);
		assertThrows(GenericException.class, () -> {
			successTalkDAO.getUserSuccessTalks(email);
		});
	}
	
	@Test
	public void getRegisteredSuccessTalks() throws IOException {
		String ccoid = "ccoid";
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        QueryBuilder ccoidQuery = QueryBuilders.matchPhraseQuery("ccoid.keyword", ccoid);
        QueryBuilder transactionType = QueryBuilders.matchPhraseQuery("registrationStatus.keyword", SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
        boolQuery.must(ccoidQuery).must(transactionType);

        sourceBuilder.query(boolQuery);
        sourceBuilder.size(1000);
        
        ElasticSearchResults<SuccesstalkUserRegEsSchema> results = new ElasticSearchResults<SuccesstalkUserRegEsSchema>();
        SuccesstalkUserRegEsSchema successtalkUserRegEsSchema = new SuccesstalkUserRegEsSchema();
        results.addDocument(successtalkUserRegEsSchema);
        when(elasticSearchDAO.query(config.getSuccessTalkUserRegistrationsIndex(), sourceBuilder, SuccesstalkUserRegEsSchema.class)).thenReturn(results);
        successTalkDAO.getRegisteredSuccessTalks(ccoid);
	}
	
	@Test
	public void getRegisteredSuccessTalksError() throws IOException {
		String ccoid = "ccoid";
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        QueryBuilder ccoidQuery = QueryBuilders.matchPhraseQuery("ccoid.keyword", ccoid);
        QueryBuilder transactionType = QueryBuilders.matchPhraseQuery("registrationStatus.keyword", SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
        boolQuery.must(ccoidQuery).must(transactionType);

        sourceBuilder.query(boolQuery);
        sourceBuilder.size(1000);
        
        ElasticSearchResults<SuccesstalkUserRegEsSchema> results = new ElasticSearchResults<SuccesstalkUserRegEsSchema>();
        SuccesstalkUserRegEsSchema successtalkUserRegEsSchema = new SuccesstalkUserRegEsSchema();
        results.addDocument(successtalkUserRegEsSchema);
        when(elasticSearchDAO.query(config.getSuccessTalkUserRegistrationsIndex(), sourceBuilder, SuccesstalkUserRegEsSchema.class)).thenThrow(IOException.class);
        assertThrows(GenericException.class, () -> {
        	successTalkDAO.getRegisteredSuccessTalks(ccoid);
		});
	}
	

	private SuccessTalk getSuccessTask() {
		SuccessTalk successTalk = new SuccessTalk();
		successTalk.setTitle("title");
		successTalk.setBookmark(true);
		successTalk.setDescription("");
		successTalk.setDocId("id");
		successTalk.setDuration("1h 30min");
		successTalk.setImageUrl("");
		successTalk.setRecordingUrl("");
		SuccessTalkSession session = new SuccessTalkSession();
		session.setDocId("");
		session.setPresenterName("John Doe");
		session.setRegion("region");
		session.setRegistrationUrl("");
		session.setScheduled(false);
		session.setSessionId("successTalkSessionId");
		session.setSessionStartDate(eventTime);
		successTalk.setSessions(Arrays.asList(session));
		successTalk.setQuarter("");
		successTalk.setStatus(SuccessTalk.SuccessTalkStatusEnum.CONFIRMED.toString());
		successTalk.setTechnicalSession(false);
		successTalk.setSuccessTalkId("");
		return successTalk;
	}
	
	private SuccesstalkUserRegEsSchema getRegistration() {
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema();
		registration.setCcoid("ccoid");
		registration.setDocId("");
		registration.setAttendedStatus(SuccesstalkUserRegEsSchema.AttendedStatusEnum.NO);
		registration.setEventStartDate(eventTime);
		registration.setEventStartDateFormatted("");
		registration.setRegistrationDate(eventTime);
		registration.setRegistrationDateFormatted("");
		registration.setRegistrationStatus(SuccesstalkUserRegEsSchema.RegistrationStatusEnum.PENDING);
		registration.setTitle("title");
		registration.setUpdated(eventTime);
		return registration;
	}
}