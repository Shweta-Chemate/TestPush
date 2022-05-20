package com.cisco.cx.training.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.PartnerPortalLookupDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.dao.UserLearningPreferencesDAO;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.entities.PartnerPortalLookUpEntity;
import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;
import com.cisco.cx.training.app.repo.BookmarkCountsRepo;
import com.cisco.cx.training.app.repo.LearningStatusRepo;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.app.service.impl.TrainingAndEnablementServiceImpl;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.GenericLearningModel;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.models.UserLearningPreference;
import com.cisco.cx.training.models.UserProfile;

@ExtendWith(SpringExtension.class)
public class TrainingAndEnablementServiceTest {

	@Mock
	private CommunityDAO communityDAO;


	@Mock
	private SuccessAcademyDAO successAcademyDAO;

	@Mock
	private SmartsheetDAO smartsheetDAO;

	@Mock
    private PropertyConfiguration config;
	
	@Mock
	private LearningBookmarkDAO learningDAO;
	
	@Mock
	private PartnerPortalLookupDAO partnerPortalLookupDAO;
	
	@Mock
	private PartnerProfileService partnerProfileService;
	
	@Mock
	private ProductDocumentationService productDocumentationService;
	
	@Mock
	private NewLearningContentDAO learningContentDAO;
	
	@Mock
	private LearningBookmarkDAO learningBookmarkDAO;
	
	@Mock
	private LearningStatusRepo learningStatusRepo;
	
	@Mock 
	UserLearningPreferencesDAO userLearningPreferencesDAO;

	@InjectMocks
	private TrainingAndEnablementService trainingAndEnablementService = new TrainingAndEnablementServiceImpl();	

	@Mock
	private BookmarkCountsRepo bookmarkCountsRepo;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	String learningTab = "Technology";
	
	private String xMasheryHeader;
	
	@BeforeEach
	public void init() throws IOException {
		this.xMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
	}
	private String loadFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
	}


	@Test
	void testGetSuccessAcademy() {
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
		List<SuccessAcademyLearning> learnings = trainingAndEnablementService.getAllSuccessAcademyLearnings(xMasheryHeader);
		assertEquals(2,learnings.size());
		assertTrue(learnings.get(0).getIsBookMarked());
		assertFalse(learnings.get(1).getIsBookMarked());
	}
	
	@Test
	void getAllCommunitiesTest() {
		Community community = getCommunity();
		List<Community> communities = Arrays.asList(community);
		when(communityDAO.getCommunities()).thenReturn(communities);
		Assertions.assertNotNull(trainingAndEnablementService.getAllCommunities());
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



	
	@Test
	void getCommunityCount() {
		Assertions.assertNotNull(trainingAndEnablementService.getCommunityCount());
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
		session.setSessionStartDate("test");
		sessions = Arrays.asList(session);
		successTalk.setSessions(sessions);
		return successTalk;
	}
	
	@Test
	void testGetSuccessAcademyLearningFilters() {
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
		assertEquals(2,learningAcademyFilter.size());
		assertEquals("1",learningAcademyFilter.get(0).getTabLocationOnUI());
		assertEquals("2",learningAcademyFilter.get(1).getTabLocationOnUI());
	}
	
	public void testBookmarkLearningForUser(){
		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("email");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);	
		BookmarkRequestSchema request = new BookmarkRequestSchema();
		request.setLearningid("1");
		request.setBookmark(true);
		when(learningDAO.createOrUpdate(Mockito.any(BookmarkResponseSchema.class), "test")).thenReturn(null);
		BookmarkResponseSchema response = trainingAndEnablementService.bookmarkLearningForUser(null, "", "test");
		
		assertEquals("1",response.getLearningid());
		assertEquals("ccoid",response.getCcoid());		
	}
		
	@Test
	void testFailureBookmarkLearningForUser(){		
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(null);
		assertThrows(NullPointerException.class, () -> {
			trainingAndEnablementService.bookmarkLearningForUser(null, xMasheryHeader, "test");
		});
	}
	
	@Test
	void getAllLearningInfoPost()
	{
		List<GenericLearningModel> cards = new ArrayList<GenericLearningModel>();
		LearningRecordsAndFiltersModel aMock = new LearningRecordsAndFiltersModel();
		aMock.setLearningData(cards);
		when(productDocumentationService.getAllLearningInfo(xMasheryHeader,"searchToken",null,"sortBy","sortOrder",learningTab,true)).thenReturn(aMock);
		LearningRecordsAndFiltersModel a = trainingAndEnablementService.getAllLearningInfoPost(xMasheryHeader,"searchToken",null,"sortBy","sortOrder",learningTab,true);
		assertEquals(0, a.getLearningData().size());
	}
	
	@Test
	void getAllLearningFiltersPost()
	{
		HashMap<String, Object> aMock = new HashMap<String, Object>();		
		when(productDocumentationService.getAllLearningFilters("searchToken",null,learningTab,true)).thenReturn(aMock);
		Map<String, Object> a = trainingAndEnablementService.getAllLearningFiltersPost("searchToken",null,learningTab,true);
		assertEquals(0, a.size());
	}
	
	NewLearningContentEntity getLearningEntity()
	{
		NewLearningContentEntity learningEntity = new NewLearningContentEntity();
		learningEntity.setId("test");
		return learningEntity;
	}
	
	private Set<String> getBookmarks() {
		Set<String> userBookmarks=new HashSet<>();
		userBookmarks.add("test");
		return userBookmarks;
	}
	
	LearningStatusEntity getLearningStatusEntity()
	{
		String testUserId = "sntccbr5@hotmail.com";
		String testPuid = "101";
		LearningStatusEntity learningStatusEntity = new LearningStatusEntity();
		learningStatusEntity.setLearningItemId("test");
		learningStatusEntity.setPuid(testPuid);
		learningStatusEntity.setUserId(testUserId);
		learningStatusEntity.setRegStatus("REGISTERED_T");
		return learningStatusEntity;
		
	}
	
	@Test
	void testULP() {
		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("email");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);
		String email = "email";
		Map<String, List<UserLearningPreference>> ulps = new HashMap<String, List<UserLearningPreference>>();
		List<UserLearningPreference> roleList = new ArrayList<UserLearningPreference>();
		UserLearningPreference roleUP = new UserLearningPreference ();
		roleUP.setName("Customer Success manager");roleList.add(roleUP);
		ulps.put("role", roleList);
		List<UserLearningPreference> tiList = new ArrayList<UserLearningPreference>();
		UserLearningPreference tiUP = new UserLearningPreference ();
		tiUP.setTimeMap(new HashMap<String,String>());tiList.add(tiUP);
		ulps.put("timeinterval", tiList);
		when(userLearningPreferencesDAO.createOrUpdateULP(userDetails.getCecId(), ulps)).thenReturn(ulps);
		trainingAndEnablementService.postUserLearningPreferences(xMasheryHeader, ulps);
		
		when(userLearningPreferencesDAO.fetchUserLearningPreferences(userDetails.getCecId())).thenReturn(ulps);
		Assertions.assertNotNull(trainingAndEnablementService.getUserLearningPreferences(xMasheryHeader));
	}
	
	@Test
	void testTopPicksPLSNonActive() throws Exception {
		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("sntccbr5@hotmail.com");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);
		HashMap<String, Object> prefMap = new HashMap<String,Object>();
		prefMap.put(Constants.SPECIALIZATION_FILTER, Stream.of(Constants.PLS_SPEC_TYPE, Constants.OFFER_SPEC_TYPE).collect(Collectors.toList()));
		when(partnerProfileService.isPLSActive(xMasheryHeader, "puid")).thenReturn(false);
		when(userLearningPreferencesDAO.getULPPreferencesDDB(Mockito.anyString())).thenReturn(prefMap);
		when(productDocumentationService.fetchMyPreferredLearnings("sntccbr5@hotmail.com", "search", null, "sortBy", "sortOrder",
				"puid", prefMap, 25, true)).thenReturn(getLearnings());
		Assertions.assertNotNull(trainingAndEnablementService.getMyPreferredLearnings(xMasheryHeader, "search", null, "sortBy", "sortOrder", "puid" , 25, true));
	}

	@Test
	void testTopPicksPLSActive() throws Exception {
		UserDetails userDetails = new UserDetails();
		userDetails.setCecId("sntccbr5@hotmail.com");
		when(partnerProfileService.fetchUserDetails(Mockito.anyString())).thenReturn(userDetails);
		HashMap<String, Object> prefMap = new HashMap<String,Object>();
		when(partnerProfileService.isPLSActive(xMasheryHeader, "puid")).thenReturn(true);
		prefMap.put(Constants.SPECIALIZATION_FILTER, Stream.of(Constants.PLS_SPEC_TYPE).collect(Collectors.toList()));
		when(userLearningPreferencesDAO.getULPPreferencesDDB(Mockito.anyString())).thenReturn(prefMap);
		when(productDocumentationService.fetchMyPreferredLearnings("sntccbr5@hotmail.com", "search", null, "sortBy", "sortOrder",
				"puid", prefMap, 25, true)).thenReturn(getLearnings());
		Assertions.assertNotNull(trainingAndEnablementService.getMyPreferredLearnings(xMasheryHeader, "search", null, "sortBy", "sortOrder", "puid" , 25, true));
	}
	private LearningRecordsAndFiltersModel getLearnings() {
		LearningRecordsAndFiltersModel resp = new LearningRecordsAndFiltersModel();
		List<GenericLearningModel> items = new ArrayList<>();
		GenericLearningModel item = new GenericLearningModel();
		item.setId("test");
		items.add(item);
		resp.setLearningData(items);
		return resp;
	}
	
}



