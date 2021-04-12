package com.cisco.cx.training.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.PartnerPortalLookupDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.entities.PartnerPortalLookUpEntity;
import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.app.service.impl.TrainingAndEnablementServiceImpl;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.models.UserProfile;

@RunWith(SpringRunner.class)
public class TrainingAndEnablementServiceTest {

	@Mock
	private CommunityDAO communityDAO;

	@Mock
	private SuccessTalkDAO successTalkDAO;

	@Mock
	private SuccessAcademyDAO successAcademyDAO;

	@Mock
	private SmartsheetDAO smartsheetDAO;

	@Mock
	private BookmarkDAO bookmarkDAO;
	
	@Mock
	private ElasticSearchDAO elasticSearchDAO;
	
	@Mock
    private PropertyConfiguration config;
	
	@Mock
	private LearningBookmarkDAO learningDAO;
	
	@Mock
	private PartnerPortalLookupDAO partnerPortalLookupDAO;
	
	@Mock
	private PartnerProfileService partnerProfileService;

	@InjectMocks
	private TrainingAndEnablementService trainingAndEnablementService = new TrainingAndEnablementServiceImpl();	

	@Test
	public void testGetSuccessAcademy() {
		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("email");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);
		SuccessAcademyLearningEntity entity1 = new SuccessAcademyLearningEntity();
		entity1.setRowId("1");
		entity1.setTitle("A");
		entity1.setAssetFacet("S");
		entity1.setAssetModel("M");
		entity1.setAssetGroup("G");
		entity1.setSupportedFormats("A,B");
		entity1.setPostedDt("11-11-2019");
		entity1.setDescription("ABC");
		entity1.setLearningLink("abc");
		entity1.setLastModifiedDtTime("11-11-2019");
		SuccessAcademyLearningEntity entity2 = new SuccessAcademyLearningEntity();
		entity2.setRowId("2");
		entity2.setTitle("A");
		entity2.setAssetFacet("S");
		entity2.setAssetModel("M");
		entity2.setAssetGroup("G");
		entity2.setSupportedFormats("A,B");
		entity2.setPostedDt("11-11-2019");
		entity2.setDescription("ABC");
		entity2.setLearningLink("abc");
		entity2.setLastModifiedDtTime("11-11-2019");
		List<SuccessAcademyLearningEntity> entityList = new ArrayList<SuccessAcademyLearningEntity>();
		entityList.add(entity1);
		entityList.add(entity2);
		when(successAcademyDAO.findAll()).thenReturn(entityList);
		Set<String> bookMarks = new HashSet<String>();
		bookMarks.add("1");
		when(learningDAO.getBookmarks(Mockito.anyString())).thenReturn(bookMarks);
		List<SuccessAcademyLearning> learnings = trainingAndEnablementService.getAllSuccessAcademyLearnings("");
		Assert.assertEquals(learnings.size(), 2);
		Assert.assertEquals(learnings.get(0).getIsBookMarked(),true);
		Assert.assertEquals(learnings.get(1).getIsBookMarked(),false);
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
		SuccessTalk successTalk = getSuccessTalk();
		when(successTalkDAO.getAllSuccessTalks()).thenReturn(Arrays.asList(successTalk));
		trainingAndEnablementService.getAllSuccessTalks();
	}

	@Test
	public void getRegisteredSuccessTalks() throws IOException {
		UserDetails userDetails = new UserDetails();
		userDetails.setEmail("email");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);
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
		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("email");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);
		String email = "email";
		BookmarkRequestSchema bookmarkRequestSchema = new BookmarkResponseSchema(); 
		BookmarkResponseSchema bookmarkResponseSchema = new BookmarkResponseSchema();
		BeanUtils.copyProperties(bookmarkRequestSchema, bookmarkResponseSchema);
		bookmarkResponseSchema.setCcoid("ccoid");
		when(bookmarkDAO.createOrUpdate(bookmarkResponseSchema)).thenReturn(bookmarkResponseSchema);
		trainingAndEnablementService.createOrUpdateBookmark(bookmarkRequestSchema, email);
	}
	
	@Test
	public void cancelUserSuccessTalkRegistration() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		String email = "email";
		String title = "title";
		Long eventStartDate = 1L;
		String puid="123";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(Mockito.anyString())).thenReturn(userDetails);
		trainingAndEnablementService.cancelUserSuccessTalkRegistration(title, eventStartDate, email,puid);
	}
	
	private UserDetailsWithCompanyList getUserDetailsWithCompanyList() {
		UserDetailsWithCompanyList userDetails=new UserDetailsWithCompanyList();
		UserProfile ciscoUserProfileSchema=new UserProfile();
		ciscoUserProfileSchema.setUserId("ccoid");
		Company company=new Company();
		company.setDemoAccount(false);
		company.setPuid("123");
		userDetails.setCiscoUserProfileSchema(ciscoUserProfileSchema);
		userDetails.setCompanyList(Arrays.asList(company));
		
		return userDetails;
	}


	@Test(expected = IOException.class)
	public void cancelUserSuccessTalkRegistrationError() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		String ccoid = "ccoid";
		String title = "title";
		Long eventStartDate = 1L;
		String puid="123";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(Mockito.anyString())).thenReturn(userDetails);
		SuccesstalkUserRegEsSchema cancelledRegistration = new SuccesstalkUserRegEsSchema(title, eventStartDate, userDetails.getCiscoUserProfileSchema().getUserId(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELLED);
		doThrow(IOException.class).when(successTalkDAO).saveSuccessTalkRegistration(cancelledRegistration);
		trainingAndEnablementService.cancelUserSuccessTalkRegistration(title, eventStartDate, ccoid,puid);
	}
	
	@Test(expected = NotAllowedException.class)
	public void cancelUserSuccessTalkRegistrationBlockDemoaccount() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		List<Company> companies=userDetails.getCompanyList();
		Company company=companies.get(0);
		company.setDemoAccount(true);
		String ccoid = "ccoid";
		String title = "title";
		Long eventStartDate = 1L;
		String puid="123";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(Mockito.anyString())).thenReturn(userDetails);
		trainingAndEnablementService.cancelUserSuccessTalkRegistration(title, eventStartDate, ccoid,puid);
	}
	
	@Test
	public void registerUserToSuccessTalkRegistration() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		String email = "email";
		String title = "title";
		Long eventStartDate = 1L;
		String puid="123";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(Mockito.anyString())).thenReturn(userDetails);
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate, userDetails.getCiscoUserProfileSchema().getEmailId(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
		when(successTalkDAO.findSuccessTalk(registration.getTitle(), registration.getEventStartDate())).thenReturn(getSuccessTalk());
		trainingAndEnablementService.registerUserToSuccessTalkRegistration(title, eventStartDate, email,puid);
	}
	
	@Test(expected = NotFoundException.class)
	public void registerUserToSuccessTalkRegistrationError() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		String email = "email";
		String title = "title";
		Long eventStartDate = 1L;
		String puid="123";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(Mockito.anyString())).thenReturn(userDetails);
		trainingAndEnablementService.registerUserToSuccessTalkRegistration(title, eventStartDate, email,puid);
	}
	
	@Test(expected = NotAllowedException.class)
	public void registerUserToSuccessTalkRegistrationBlockDemoaccount() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		List<Company> companies=userDetails.getCompanyList();
		Company company=companies.get(0);
		company.setDemoAccount(true);
		String email = "email";
		String title = "title";
		Long eventStartDate = 1L;
		String puid="123";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(Mockito.anyString())).thenReturn(userDetails);
		trainingAndEnablementService.registerUserToSuccessTalkRegistration(title, eventStartDate, email,puid);
	}
	
	@Test(expected = IOException.class)
	public void registerUserToSuccessTalkRegistrationSmartsheetError() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		String email = "email";
		String title = "title";
		Long eventStartDate = 1L;
		String puid="123";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(Mockito.anyString())).thenReturn(userDetails);
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate, userDetails.getCiscoUserProfileSchema().getEmailId(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.PENDING);
		doThrow(IOException.class).when(successTalkDAO).saveSuccessTalkRegistration(Mockito.any(SuccesstalkUserRegEsSchema.class));
		when(successTalkDAO.findSuccessTalk(registration.getTitle(), registration.getEventStartDate())).thenReturn(getSuccessTalk());
		trainingAndEnablementService.registerUserToSuccessTalkRegistration(title, eventStartDate, email,puid);
	}
	

	@Test
	public void fetchSuccessTalkRegistrationDetails() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		String title = "title";
		Long eventStartDate = 1L;
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate, userDetails.getCiscoUserProfileSchema().getEmailId(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
		SuccessTalk successTalk = getSuccessTalk();
		when(successTalkDAO.findSuccessTalk(registration.getTitle(), registration.getEventStartDate())).thenReturn(successTalk);
		trainingAndEnablementService.fetchSuccessTalkRegistrationDetails(registration, userDetails.getCiscoUserProfileSchema());
	}
	
	@Test(expected = GenericException.class)
	public void fetchSuccessTalkRegistrationDetailsError() throws Exception {
		UserDetailsWithCompanyList userDetails=getUserDetailsWithCompanyList();
		String title = "title";
		Long eventStartDate = 1L;
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate, userDetails.getCiscoUserProfileSchema().getEmailId(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
		when(successTalkDAO.findSuccessTalk(registration.getTitle(), registration.getEventStartDate())).thenThrow(IOException.class);
		trainingAndEnablementService.fetchSuccessTalkRegistrationDetails(registration, userDetails.getCiscoUserProfileSchema());
	}
	
	@Test
	public void getCommunityCount() {
		trainingAndEnablementService.getCommunityCount();
	}
	
	@Test
	public void getSuccessTalkCount() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder includeCancelledQuery = QueryBuilders.matchPhraseQuery("status.keyword", SuccessTalk.SuccessTalkStatusEnum.CANCELLED);
		boolQuery.mustNot(includeCancelledQuery);
		sourceBuilder.query(boolQuery);
		when(elasticSearchDAO.countRecordsWithFilter(config.getSuccessTalkIndex(), sourceBuilder)).thenReturn(1l);
		trainingAndEnablementService.getSuccessTalkCount();
	}
	
	@Test(expected = GenericException.class)
	public void getSuccessTalkCountError() throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		QueryBuilder includeCancelledQuery = QueryBuilders.matchPhraseQuery("status.keyword", SuccessTalk.SuccessTalkStatusEnum.CANCELLED);
		boolQuery.mustNot(includeCancelledQuery);
		sourceBuilder.query(boolQuery);
		when(elasticSearchDAO.countRecordsWithFilter(config.getSuccessTalkIndex(), sourceBuilder)).thenThrow(IOException.class);
		trainingAndEnablementService.getSuccessTalkCount();
	}
	
	@Test
	public void getIndexCounts() {
		trainingAndEnablementService.getIndexCounts();
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

	private SuccessTalk getSuccessTalk() {
		SuccessTalk successTalk = new SuccessTalk();
		successTalk.setBookmark(true);
		successTalk.setDescription("");
		successTalk.setDocId("id");
		successTalk.setDuration("1h 30min");
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
		sessions = Arrays.asList(session);
		successTalk.setSessions(sessions);
		return successTalk;
	}
	
	@Test
	public void testGetSuccessAcademyLearningFilters() {
		Object[] str1 = new Object[2];
		str1[0]="A";
		str1[1]="a";
		
		Object[] str2 = new Object[2];
		str2[0]="B";
		str2[1]="b";
		
		List<Object[]> filtersList = new ArrayList<Object[]>();
		filtersList.add(str1);
		filtersList.add(str2);
		when(successAcademyDAO.getLearningFilters()).thenReturn(filtersList);
		PartnerPortalLookUpEntity entity1 = new PartnerPortalLookUpEntity();
		entity1.setPartnerPortalKey("A");
		entity1.setPartnerPortalKeyValue("1");
		entity1.setRowId("1");
		entity1.setDescription("Test");
		entity1.setCreatedBy("abc");
		entity1.setCreatedDtTime(new Date(System.currentTimeMillis()));
		entity1.setUpdatedBy("abc");
		entity1.setUpdatedDtTime(new Date(System.currentTimeMillis()));
		PartnerPortalLookUpEntity entity2 = new PartnerPortalLookUpEntity();
		entity2.setPartnerPortalKey("B");
		entity2.setPartnerPortalKeyValue("2");
		entity2.setRowId("1");
		entity2.setDescription("Test");
		entity2.setCreatedBy("abc");
		entity2.setCreatedDtTime(new Date(System.currentTimeMillis()));
		entity2.setUpdatedBy("abc");
		entity2.setUpdatedDtTime(new Date(System.currentTimeMillis()));
		List<PartnerPortalLookUpEntity> entityList = new ArrayList<PartnerPortalLookUpEntity>();
		entityList.add(entity1);
		entityList.add(entity2);
		when(partnerPortalLookupDAO.getTabLocations()).thenReturn(entityList);	
		
		List<SuccessAcademyFilter> learningAcademyFilter = trainingAndEnablementService.getSuccessAcademyFilters();
		Assert.assertEquals(learningAcademyFilter.size(), 2);
		Assert.assertEquals(learningAcademyFilter.get(0).getTabLocationOnUI(), "1");
		Assert.assertEquals(learningAcademyFilter.get(1).getTabLocationOnUI(), "2");
	}
	
	public void testBookmarkLearningForUser(){
		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("email");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);	
		BookmarkRequestSchema request = new BookmarkRequestSchema();
		request.setLearningid("1");
		request.setBookmark(true);
		when(learningDAO.createOrUpdate(Mockito.any(BookmarkResponseSchema.class))).thenReturn(null);
		BookmarkResponseSchema response = trainingAndEnablementService.bookmarkLearningForUser(null, "");
		
		Assert.assertEquals(response.getLearningid(),"1");
		Assert.assertEquals(response.getCcoid(),"ccoid");		
	}
		
	@Test(expected = BadRequestException.class)
	public void testFailureBookmarkLearningForUser(){		
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(null);			
		trainingAndEnablementService.bookmarkLearningForUser(null, "");
	}
}
