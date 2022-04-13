package com.cisco.cx.training.app.services.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.repo.LearningStatusRepo;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.ProductDocumentationService;
import com.cisco.cx.training.app.service.impl.LearningContentServiceImpl;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.Company;
import com.cisco.cx.training.models.LearningMap;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.LearningStatusSchema.Registration;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.models.UserRole;

@ExtendWith(SpringExtension.class)
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
	
	@Mock
	private ProductDocumentationService productDocumentationService;

	@Autowired
	ResourceLoader resourceLoader;

	private String XMasheryHeader;

	private String puid = "101";

	@InjectMocks
	private LearningContentService learningContentService=new LearningContentServiceImpl(); 

	@BeforeEach
	public void init() throws IOException {
		this.XMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));

	}

	@Test
	public void testFetchPIWs() {
		List<NewLearningContentEntity> result = getLearningEntities();
		Set<String> userBookmarks=getBookmarks();
		when(learningContentDAO.listPIWs(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString())).thenReturn(result);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid("test", "test", "test")).thenReturn(new LearningStatusEntity());
		assertNotNull(learningContentService.fetchPIWs("test", "test", "test", "test", "test:test", "test"));
	}

	@Test
	public void testFetchSuccesstalks() {
		List<NewLearningContentEntity> result = getLearningEntities();
		Set<String> userBookmarks=getBookmarks();
		when(learningContentDAO.fetchSuccesstalks(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString())).thenReturn(result);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid("test", "test", "test")).thenReturn(new LearningStatusEntity());
		assertNotNull(learningContentService.fetchSuccesstalks("test", "test", "test", "test:test", "test"));
	}

	@Test
	public void getIndexCounts() {
		learningContentService.getIndexCounts();
	}

	@Test
	public void testFetchNewLearningContent()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		String testUserId = "testUserId";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchNewLearningContent(Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserId(testUserId)).thenReturn(learningStatusList);
		learningContentService.fetchNewLearningContent(testUserId, testFilter);
		when(learningContentDAO.fetchNewLearningContent(Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchNewLearningContent(testUserId, testFilter);
		});
	}

	@Test
	public void testGetViewMoreNewFiltersWithCount()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		HashMap<String, Object> filterCounts = getTestFilterCounts();
		when(learningContentDAO.getViewMoreNewFiltersWithCount(Mockito.any())).thenReturn(filterCounts);
		learningContentService.getViewMoreNewFiltersWithCount(testFilter);
		when(learningContentDAO.getViewMoreNewFiltersWithCount(Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.getViewMoreNewFiltersWithCount(testFilter);
		});
	}

	@Test
	public void testUpdateUserStatusLearningEntityNull()
	{
		LearningStatusSchema testLearningStatusSchema = getLearningStatusSchema();
		LearningStatusEntity entity =  getLearningStatusEntity();
		String testUserId = "sntccbr5@hotmail.com";
		when(partnerProfileService.fetchUserDetailsWithCompanyList(this.XMasheryHeader)).thenReturn(getUserDetails());
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid(testLearningStatusSchema.getLearningItemId(), testUserId, this.puid)).thenReturn(null);
		when(learningStatusRepo.save(Mockito.any())).thenReturn(entity);
		assertNotNull(learningContentService.updateUserStatus(testUserId, this.puid, getLearningStatusSchema(), this.XMasheryHeader));
	}

	@Test
	public void testUpdateUserStatusLearningEntityNotNull()
	{
		LearningStatusSchema testLearningStatusSchema = getLearningStatusSchema();
		String testUserId = "sntccbr5@hotmail.com";
		LearningStatusEntity entity = getLearningStatusEntity();
		when(partnerProfileService.fetchUserDetailsWithCompanyList(this.XMasheryHeader)).thenReturn(getUserDetails());
		when(learningStatusRepo.findByLearningItemIdAndUserIdAndPuid(testLearningStatusSchema.getLearningItemId(), testUserId, this.puid)).thenReturn(getLearningStatusEntity());
		when(learningStatusRepo.save(Mockito.any())).thenReturn(entity);
		assertNotNull(learningContentService.updateUserStatus(testUserId, this.puid, getLearningStatusSchema(), this.XMasheryHeader));
	}

	@Test
	public void testfetchRecentlyViewedContent()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		String testUserId = "testUserId";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchRecentlyViewedContent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserId(testUserId)).thenReturn(learningStatusList);
		learningContentService.fetchRecentlyViewedContent(testUserId, testFilter);
		when(learningContentDAO.fetchRecentlyViewedContent(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchRecentlyViewedContent(testUserId, testFilter);
		});
	}

	@Test
	public void testGetRecentlyViewedFiltersWithCount()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		HashMap<String, Object> filterCounts = getTestFilterCounts();
		String testUserId = "testUserId";
		when(learningContentDAO.getRecentlyViewedFiltersWithCount(Mockito.any(), Mockito.any())).thenReturn(filterCounts);
		learningContentService.getRecentlyViewedFiltersWithCount(testUserId, testFilter);
		when(learningContentDAO.getRecentlyViewedFiltersWithCount(Mockito.any() ,Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.getRecentlyViewedFiltersWithCount(testUserId, testFilter);
		});
	}

	@Test
	public void testFetchBookMarkedContent()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		String testUserId = "testUserId";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchFilteredContent(Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Map<String,Object> userBookmarks=getBookmarksWithTime();
		when(learningBookmarkDAO.getBookmarksWithTime(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserId(testUserId)).thenReturn(learningStatusList);
		learningContentService.fetchBookMarkedContent(testUserId, testFilter);
		when(learningContentDAO.fetchFilteredContent(Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchBookMarkedContent(testUserId, testFilter);
		});
	}

	@Test
	public void testGetBookmarkedFiltersWithCount()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		HashMap<String, Object> filterCounts = getTestFilterCounts();
		String testUserId = "testUserId";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchFilteredContent(Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserId(testUserId)).thenReturn(learningStatusList);
		when(learningContentDAO.getBookmarkedFiltersWithCount(Mockito.any(), Mockito.any())).thenReturn(filterCounts);
		learningContentService.getBookmarkedFiltersWithCount(testUserId, testFilter);
		when(learningContentDAO.getBookmarkedFiltersWithCount(Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.getBookmarkedFiltersWithCount(testUserId, testFilter);
		});
	}

	@Test
	public void testFetchUpcomingContent()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		String testUserId = "testUserId";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchUpcomingContent(Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserId(testUserId)).thenReturn(learningStatusList);
		learningContentService.fetchUpcomingContent(testUserId, testFilter);
		when(learningContentDAO.fetchUpcomingContent(Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchUpcomingContent(testUserId, testFilter);
		});
	}

	@Test
	public void testGetUpcomingFiltersWithCount()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		HashMap<String, Object> filterCounts = getTestFilterCounts();
		when(learningContentDAO.getUpcomingFiltersWithCount(Mockito.any())).thenReturn(filterCounts);
		learningContentService.getUpcomingFiltersWithCount(testFilter);
		when(learningContentDAO.getUpcomingFiltersWithCount(Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.getUpcomingFiltersWithCount(testFilter);
		});
	}

	@Test
	public void testFetchCXInsightsContent()
	{
		String testUserId = "testUserId";
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		String searchToken = "test";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchCXInsightsContent(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		learningContentService.fetchCXInsightsContent(testUserId, testFilter, searchToken, null, null);
		when(learningContentDAO.fetchCXInsightsContent(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchCXInsightsContent(testUserId, testFilter, searchToken, null, null);
		});
	}

	@Test
	public void testFetchPopularContent()
	{
		//popular across partners call
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		String testUserId = "testUserId";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchPopularAcrossPartnersContent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserId(testUserId)).thenReturn(learningStatusList);
		learningContentService.fetchPopularContent(testUserId, testFilter, "popularAcrossPartners", "puid");
		when(learningContentDAO.fetchPopularAcrossPartnersContent(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching popular across partners learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchPopularContent(testUserId, testFilter, "popularAcrossPartners", "puid");
		});

		//popular at partner company call
		when(learningContentDAO.fetchPopularAtPartnerContent(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		learningContentService.fetchPopularContent(testUserId, testFilter, "popularAtPartner", "puid");
		when(learningContentDAO.fetchPopularAtPartnerContent(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching popular at partner company learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchPopularContent(testUserId, testFilter, "popularAtPartner", "puid");
		});
	}
	
	@Test
	public void testGetCXInsightsFiltersWithCount()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		HashMap<String, Object> filterCounts = getTestFilterCounts();
		when(learningContentDAO.getCXInsightsFiltersWithCount(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(filterCounts);
		learningContentService.getCXInsightsFiltersWithCount("test", "test", testFilter);
		when(learningContentDAO.getCXInsightsFiltersWithCount(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching CX Insights filters"));
		assertThrows(Exception.class, () -> {
			learningContentService.getCXInsightsFiltersWithCount("test", "test", testFilter);
		});
	}

	@Test
	public void testGetPopularContentFiltersWithCount()
	{
		//popular across partners test
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		HashMap<String, Object> filterCounts = getTestFilterCounts();
		when(learningContentDAO.getPopularAcrossPartnersFiltersWithCount(Mockito.any(), Mockito.any())).thenReturn(filterCounts);
		learningContentService.getPopularContentFiltersWithCount(testFilter, "puid", "popularAcrossPartners", "test");
		when(learningContentDAO.getPopularAcrossPartnersFiltersWithCount(Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching popular across partners filters"));
		assertThrows(Exception.class, () -> {
			learningContentService.getPopularContentFiltersWithCount(testFilter, "puid", "popularAcrossPartners", "test");
		});

		//populat at partner company test
		when(learningContentDAO.getPopularAtPartnerFiltersWithCount(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(filterCounts);
		learningContentService.getPopularContentFiltersWithCount(testFilter, "puid", "popularAtPartner", "test");
		when(learningContentDAO.getPopularAtPartnerFiltersWithCount(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching popular at partner company filters"));
		assertThrows(Exception.class, () -> {
			learningContentService.getPopularContentFiltersWithCount(testFilter, "puid", "popularAtPartner", "test");
		});
	}

	@Test
	public void testGetLearningMap()
	{
		LearningMap learningMap = getLearningMap();
		when(learningContentDAO.getLearningMap(Mockito.anyString(), Mockito.anyString())).thenReturn(learningMap);
		learningContentService.getLearningMap("test", "test");
		when(learningContentDAO.getLearningMap(Mockito.anyString(), Mockito.anyString())).thenThrow(new GenericException("There was a problem in fetching CX Insights learning content"));
		assertThrows(Exception.class, () -> {
			learningContentService.getLearningMap("test", "test");
		});
	}

	@Test
	public void testFetchFeaturedContent()
	{
		String testUserId = "testUserId";
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentDAO.fetchFeaturedContent(Mockito.any(), Mockito.any())).thenReturn(learningEntityList);
		Set<String> userBookmarks=getBookmarks();
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(userBookmarks);
		List<LearningStatusEntity> learningStatusList = new ArrayList<>();
		learningStatusList.add(getLearningStatusEntity());
		when(learningStatusRepo.findByUserId(testUserId)).thenReturn(learningStatusList);
		learningContentService.fetchFeaturedContent(testUserId, testFilter);
		when(learningContentDAO.fetchFeaturedContent(Mockito.any(), Mockito.any())).thenThrow(new GenericException("There was a problem in fetching featured content"));
		assertThrows(Exception.class, () -> {
			learningContentService.fetchFeaturedContent(testUserId, testFilter);
		});
	}
	
	@Test
	public void testGetFeaturedContentFiltersWithCount()
	{
		HashMap<String, Object> testFilter = getTestFiltersSelected();
		HashMap<String, Object> filterCounts = getTestFilterCounts();
		when(learningContentDAO.getFeaturedFiltersWithCount(Mockito.any())).thenReturn(filterCounts);
		learningContentService.getFeaturedFiltersWithCount(testFilter);
		when(learningContentDAO.getFeaturedFiltersWithCount(Mockito.any())).thenThrow(new GenericException("There was a problem in fetching featured filters"));
		assertThrows(Exception.class, () -> {
			learningContentService.getFeaturedFiltersWithCount(testFilter);
		});
	}
	
	private LearningMap getLearningMap() {
		LearningMap learningMap = new LearningMap();
		learningMap.setId("test");
		learningMap.setTitle("test");
		learningMap.setModule_count("3");
		learningMap.setLearning_type("learningmap");
		return learningMap;
	}

	private Set<String> getBookmarks() {
		Set<String> userBookmarks=new HashSet<>();
		userBookmarks.add("test");
		return userBookmarks;
	}

	private Map<String, Object> getBookmarksWithTime() {
		Map<String, Object> userBookmarks=new HashMap<>();
		userBookmarks.put("test", (long) 87987868);
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

	private HashMap<String, Object> getTestFiltersSelected() {
		HashMap<String, Object> testFilter = new HashMap<>();
		List<String> valueList=new ArrayList<>();
		valueList.add("test");
		testFilter.put("test", valueList);
		HashMap<String, Map<String, List<String>>> stFilter= new HashMap<>();
		HashMap<String, List<String>> ucList=new HashMap<>();
		List<String> ptList=new ArrayList<>();
		ptList.add("test");
		ucList.put("test", ptList);
		stFilter.put("test", ucList);
		testFilter.put("Success Tracks", stFilter);
		return testFilter;
	}

	private HashMap<String, Object> getTestFilterCounts() {
		HashMap<String, Object> filterCounts = new HashMap<>();
		HashMap<String, String> testRegionCount = new HashMap<>();
		testRegionCount.put("AMER", "1");
		filterCounts.put("Live Events" , testRegionCount);
		HashMap<String, String> testContentCount = new HashMap<>();
		testContentCount.put("PDF", "1");
		filterCounts.put("Content Type" , testContentCount);
		HashMap<String, String> testLanguageCount = new HashMap<>();
		testLanguageCount.put("English", "1");
		filterCounts.put("Language", testLanguageCount);
		return filterCounts;
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

}

