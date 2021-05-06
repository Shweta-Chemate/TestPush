package com.cisco.cx.training.app.services.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.LearningStatusRepo;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.impl.LearningContentServiceImpl;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.LearningStatusSchema.Registration;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.models.UserRole;

@RunWith(SpringRunner.class)
public class LearningContentServiceTest {

	@Mock
	private NewLearningContentDAO learningContentDAO;
	
	@Mock
	private SuccessAcademyDAO successAcademyDAO;
	
	@Mock
	private PartnerProfileService partnerProfileService;
	
	@Mock
	private LearningBookmarkDAO learningBookmarkDAO;
	
	@Mock
	private LearningStatusRepo learningStatusRepo;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	private String XMasheryHeader;
	
	private String puid = "101";
	
	@InjectMocks
	private LearningContentService learningContentService=new LearningContentServiceImpl(); 
	
	@Before
	public void init() throws IOException {
		this.XMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));

	}
	
	@Test
	public void testFetchPIWs() {
		List<NewLearningContentEntity> result = getLearningEntities();
		Set<String> userBookmarks=getBookmarks();
		when(learningContentDAO.listPIWs(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(result);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid("test", "test", "test")).thenReturn(new LearningStatusEntity());
	    assertNotNull(learningContentService.fetchPIWs("test","101", "test","test","test","test:test","test"));
	}
	
	@Test
	public void testFetchSuccesstalks() {
		List<NewLearningContentEntity> result = getLearningEntities();
		Set<String> userBookmarks=getBookmarks();
		when(learningContentDAO.fetchSuccesstalks(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(result);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid("test", "test", "test")).thenReturn(new LearningStatusEntity());
	    assertNotNull(learningContentService.fetchSuccesstalks("test","101", "test","test","test:test","test"));
	}

	private List<NewLearningContentEntity> getLearningEntities() {
		List<NewLearningContentEntity> resp=new ArrayList<NewLearningContentEntity>();
		NewLearningContentEntity learningContentEntity=new NewLearningContentEntity();
		learningContentEntity.setId("test");
		learningContentEntity.setTitle("test");
		learningContentEntity.setDuration("test");
		learningContentEntity.setPresenterName("test");
		learningContentEntity.setRegistrationUrl("test");
		learningContentEntity.setRegion("test");
		learningContentEntity.setSessionStartDate(new Timestamp(System.currentTimeMillis()));
		resp.add(learningContentEntity);
		return resp;
	}
	
	@Test
	public void getIndexCounts() {
		when(learningContentDAO.getSuccessTalkCount()).thenReturn(2);
		when(successAcademyDAO.count()).thenReturn((long) 2);
		learningContentService.getIndexCounts();
	}
	
	@Test
	public void testGetViewMoreNewFiltersWithCount()
	{
		String testFilter = "test:test";
		String select="test";
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testRegionCount = new HashMap<>();
		testRegionCount.put("AMER", "1");
		filterCounts.put("Live Events" , testRegionCount);
		HashMap<String, String> testContentCount = new HashMap<>();
		testContentCount.put("PDF", "1");
		filterCounts.put("Content Type" , testContentCount);
		HashMap<String, String> testLanguageCount = new HashMap<>();
		testLanguageCount.put("English", "1");
		filterCounts.put("Language" , testLanguageCount);
		when(learningContentDAO.getViewMoreNewFiltersWithCount(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(filterCounts);
		learningContentService.getViewMoreNewFiltersWithCount(testFilter, filterCounts, select);
	}
	
	@Test
	public void testUpdateUserStatusLearningEntityNull()
	{
		LearningStatusSchema testLearningStatusSchema = getLearningStatusSchema();
		String testUserId = "sntccbr5@hotmail.com";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(this.XMasheryHeader)).thenReturn(getUserDetails());
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid(testLearningStatusSchema.getLearningItemId(),testUserId, this.puid)).thenReturn(null);
		learningContentService.updateUserStatus(testUserId, this.puid, getLearningStatusSchema(), this.XMasheryHeader);
	}
	
	@Test
	public void testUpdateUserStatusLearningEntityNotNull()
	{
		LearningStatusSchema testLearningStatusSchema = getLearningStatusSchema();
		String testUserId = "sntccbr5@hotmail.com";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(this.XMasheryHeader)).thenReturn(getUserDetails());
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid(testLearningStatusSchema.getLearningItemId(),testUserId, this.puid)).thenReturn(getLearningStatusEntity());
		learningContentService.updateUserStatus(testUserId, this.puid, getLearningStatusSchema(), this.XMasheryHeader);
	}
	
	@Test
	public void testfetchRecentlyViewedContent()
	{
		String testFilter = "test:test";
		String testUserId = "testUserId";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchRecentlyViewedContent(Mockito.anyString(),Mockito.anyString(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserIdAndPuid(testUserId, this.puid)).thenReturn(learningStatusList);
		learningContentService.fetchRecentlyViewedContent(this.puid, testUserId, testFilter);
	}
	
	@Test
	public void testGetRecentlyViewedFiltersWithCount()
	{
		String testFilter = "test:test";
		String testUserId = "testUserId";
		String select="test";
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testRegionCount = new HashMap<>();
		testRegionCount.put("AMER", "1");
		filterCounts.put("Live Events" , testRegionCount);
		HashMap<String, String> testContentCount = new HashMap<>();
		testContentCount.put("PDF", "1");
		filterCounts.put("Content Type" , testContentCount);
		HashMap<String, String> testLanguageCount = new HashMap<>();
		testLanguageCount.put("English", "1");
		filterCounts.put("Language" , testLanguageCount);
		when(learningContentDAO.getRecentlyViewedFiltersWithCount(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(filterCounts);
		learningContentService.getRecentlyViewedFiltersWithCount(this.puid, testUserId, testFilter, filterCounts, select);
	}
	
	@Test
	public void testFetchBookMarkedContent()
	{
		String testUserId = "testUserId";
		String testFilter = "test:test";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchFilteredContent(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserIdAndPuid(testUserId, this.puid)).thenReturn(learningStatusList);
		learningContentService.fetchBookMarkedContent(this.puid, testUserId, testFilter);
	}
	
	@Test
	public void testGetBookmarkedFiltersWithCount()
	{
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testRegionCount = new HashMap<>();
		testRegionCount.put("AMER", "1");
		filterCounts.put("Live Events" , testRegionCount);
		String select="test";
		HashMap<String, String> testContentCount = new HashMap<>();
		testContentCount.put("PDF", "1");
		filterCounts.put("Content Type" , testContentCount);
		HashMap<String, String> testLanguageCount = new HashMap<>();
		testLanguageCount.put("English", "1");
		filterCounts.put("Language" , testLanguageCount);
		String testUserId = "testUserId";
		String testFilter = "test:test";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchFilteredContent(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserIdAndPuid(testUserId, this.puid)).thenReturn(learningStatusList);
		when(learningContentDAO.getBookmarkedFiltersWithCount(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(filterCounts);
		learningContentService.getBookmarkedFiltersWithCount(this.puid, testUserId, testFilter, filterCounts, select);
	}
	
	@Test
	public void testFetchUpcomingContent()
	{
		String testUserId = "testUserId";
		String testFilter = "test:test";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchUpcomingContent(Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserIdAndPuid(testUserId, this.puid)).thenReturn(learningStatusList);
		learningContentService.fetchUpcomingContent(this.puid, testUserId, testFilter);
	}
	
	@Test
	public void testGetUpcomingFiltersWithCount()
	{
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testRegionCount = new HashMap<>();
		String select="test";
		testRegionCount.put("AMER", "1");
		filterCounts.put("Live Events" , testRegionCount);
		HashMap<String, String> testContentCount = new HashMap<>();
		testContentCount.put("PDF", "1");
		filterCounts.put("Content Type" , testContentCount);
		HashMap<String, String> testLanguageCount = new HashMap<>();
		testLanguageCount.put("English", "1");
		filterCounts.put("Language" , testLanguageCount);
		String testFilter = "test:test";
		when(learningContentDAO.getUpcomingFiltersWithCount(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(filterCounts);
		learningContentService.getUpcomingFiltersWithCount(testFilter, filterCounts, select);
	}
	
	private Set<String> getBookmarks() {
		Set<String> userBookmarks=new HashSet<>();
		userBookmarks.add("test");
		return userBookmarks;
	}
	
	private String loadFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
	}
	
	private UserDetailsWithCompanyList getUserDetails()
	{
		UserDetailsWithCompanyList userDetails = new UserDetailsWithCompanyList();
		List<Company> companyList = new ArrayList<>();
		companyList.add(getCompany());
		userDetails.setCompanyList(companyList);
		return userDetails;
	}
	
	private Company getCompany()
	{
		Company company = new Company();
		company.setPuid(this.puid);
		company.setRoleList(getUserRoles());
		return company;
	}
	
	private List<UserRole> getUserRoles()
	{
		List<UserRole> userRoles = new ArrayList<>();
		UserRole userRole = new UserRole();
		userRole.setRoleId(1);
		List<String> resourceList = new ArrayList<>();
		resourceList.add(Constants.RESOURCE_ID);
		userRole.setResourceList(resourceList);
		userRoles.add(userRole);
		
		return userRoles;
	}
	
	private LearningStatusSchema getLearningStatusSchema()
	{
		LearningStatusSchema learningStatusSchema = new LearningStatusSchema();
		learningStatusSchema.setLearningItemId("test");
		learningStatusSchema.setRegStatus(Registration.REGISTERED_T);
		learningStatusSchema.setViewed(true);
		return learningStatusSchema;
	}
	
	LearningStatusEntity getLearningStatusEntity()
	{
		String testUserId = "sntccbr5@hotmail.com";
		LearningStatusEntity learningStatusEntity = new LearningStatusEntity();
		learningStatusEntity.setLearningItemId("test");
		learningStatusEntity.setPuid(this.puid);
		learningStatusEntity.setUserId(testUserId);
		learningStatusEntity.setRegStatus("REGISTERED_T");
		return learningStatusEntity;
		
	}
	
	NewLearningContentEntity getLearningEntity()
	{
		NewLearningContentEntity learningEntity = new NewLearningContentEntity();
		learningEntity.setId("test");
		return learningEntity;
	}
	
}

