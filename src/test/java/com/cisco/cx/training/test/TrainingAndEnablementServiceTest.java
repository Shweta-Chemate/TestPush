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
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.LearningDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.app.service.impl.TrainingAndEnablementServiceImpl;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;

@RunWith(SpringRunner.class)
public class TrainingAndEnablementServiceTest {

	@Mock
	private CommunityDAO communityDAO;

	@Mock
	private SuccessTalkDAO successTalkDAO;

	@Mock
	private LearningDAO learningDAO;

	@Mock
	private SmartsheetDAO smartsheetDAO;

	@Mock
	private BookmarkDAO bookmarkDAO;
	
	@Mock
	private ElasticSearchDAO elasticSearchDAO;
	
	@Mock
    private PropertyConfiguration config;

	@InjectMocks
	private TrainingAndEnablementService trainingAndEnablementService = new TrainingAndEnablementServiceImpl();


	@Test
	public void testGetLearnings() {
		trainingAndEnablementService.getAllLearning();
	}

	@Test
	public void testInsertLearnings() {
		trainingAndEnablementService.insertLearning(getLearning());
	}

	@Test
	public void getFilteredLearning() {
		trainingAndEnablementService.getFilteredLearning("solution", "usecase");
	}

	@Test
	public void insertLearning() {
		trainingAndEnablementService.getAllLearning();
	}

	@Test
	public void getAllCommunitiesTest() {
		Community community = getCommunity();
		List<Community> communities = Arrays.asList(community);
		when(communityDAO.getCommunities()).thenReturn(communities);
		trainingAndEnablementService.getAllCommunities();
	}


	@Test
	public void getAllSuccessTalksTest() {
		SuccessTalk successTalk = getSuccessTask();
		when(successTalkDAO.getAllSuccessTalks()).thenReturn(Arrays.asList(successTalk));
		trainingAndEnablementService.getAllSuccessTalks();
	}

	@Test
	public void insertSuccessTalksTest() {
		SuccessTalk successTalk = getSuccessTask();
		when(successTalkDAO.insertSuccessTalk(successTalk)).thenReturn(successTalk);
		trainingAndEnablementService.insertSuccessTalk(successTalk);
	}

	@Test
	public void getRegisteredSuccessTalks() throws IOException {
		String email = "email";
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        QueryBuilder emailQuery = QueryBuilders.matchPhraseQuery("email.keyword", email);
        QueryBuilder transactionType = QueryBuilders.matchPhraseQuery("registrationStatus.keyword", SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
        boolQuery.must(emailQuery).must(transactionType);

        sourceBuilder.query(boolQuery);
        sourceBuilder.size(1000);
        
        ElasticSearchResults<SuccesstalkUserRegEsSchema> results = new ElasticSearchResults<SuccesstalkUserRegEsSchema>();
        SuccesstalkUserRegEsSchema successtalkUserRegEsSchema = new SuccesstalkUserRegEsSchema();
        results.addDocument(successtalkUserRegEsSchema);
        when(elasticSearchDAO.query(config.getSuccessTalkUserRegistrationsIndex(), sourceBuilder, SuccesstalkUserRegEsSchema.class)).thenReturn(results);
        trainingAndEnablementService.getUserSuccessTalks(email);
	}
	
	
	@Test
	public void createOrUpdateBookmark() {
		String email = "email";
		BookmarkRequestSchema bookmarkRequestSchema = new BookmarkResponseSchema(); 
		BookmarkResponseSchema bookmarkResponseSchema = new BookmarkResponseSchema();
		BeanUtils.copyProperties(bookmarkRequestSchema, bookmarkResponseSchema);
		bookmarkResponseSchema.setEmail(email );
		when(bookmarkDAO.createOrUpdate(bookmarkResponseSchema)).thenReturn(bookmarkResponseSchema);
		trainingAndEnablementService.createOrUpdateBookmark(bookmarkRequestSchema, email);
	}
	
	@Test
	public void cancelUserSuccessTalkRegistration() throws Exception {
		String email = "email";
		String title = "title";
		Long eventStartDate = 1L;
		trainingAndEnablementService.cancelUserSuccessTalkRegistration(title, eventStartDate, email);
	}
	
	@Test
	public void registerUserToSuccessTalkRegistration() throws Exception {
		String email = "email";
		String title = "title";
		Long eventStartDate = 1L;
		trainingAndEnablementService.registerUserToSuccessTalkRegistration(title, eventStartDate, email);
	}
	
	private Learning getLearning() {
		Learning learning = new Learning();
		learning.setAlFrescoId("alFrescoId");
		return learning;
	}

	private Community getCommunity() {
		Community community = new Community();
		community.setDocId("1234");
		community.setName("community");
		community.setDescription("hello");
		community.setSolution("solution");
		community.setUrl("http://df.fdsds.com");
		community.setUsecase("IBN");
		return community;
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
		session.setScheduled(false);
		session.setSessionId("");
		session.setSessionStartDate(00L);
		Arrays.asList(session);
		successTalk.setSessions(sessions);
		return successTalk;
	}
}
