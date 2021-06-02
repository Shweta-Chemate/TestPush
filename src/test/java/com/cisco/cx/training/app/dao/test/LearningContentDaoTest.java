package com.cisco.cx.training.app.dao.test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.impl.FilterCountsDAOImpl;
import com.cisco.cx.training.app.dao.impl.NewLearningContentDAOImpl;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.LearningContentItem;


@ExtendWith(SpringExtension.class)
public class LearningContentDaoTest {

	@Mock
	private NewLearningContentRepo learningContentRepo;
	
	@Mock
	private FilterCountsDAOImpl filterCountsDao;
	
	@InjectMocks
	private NewLearningContentDAO learningContentDAO=new NewLearningContentDAOImpl();
	
	@Test
	public void testListPIWs() {
		List<NewLearningContentEntity> piwList = new ArrayList<>();
		LinkedHashMap<String, String> filter = new LinkedHashMap();
		filter.put("testKey", "testValue");
		Specification<NewLearningContentEntity> specification = new SpecificationBuilderPIW().filter(filter,"testSearch","testRegion");
		when(learningContentRepo.findAll(specification, Sort.by(Sort.Direction.fromString("asc"), "testField"))).thenReturn(piwList);
		learningContentDAO.listPIWs("testRegion", "testField", "asc", filter, "testSearch");
	}
	
	@Test
	public void testFetchSuccesstalks() {
		List<NewLearningContentEntity> successtalkList = new ArrayList<>();
		LinkedHashMap<String, String> filter = new LinkedHashMap();
		filter.put("testKey", "testValue");
		Specification<NewLearningContentEntity> specification = new SpecificationBuilderPIW().filter(filter,"testSearch","testRegion");
		when(learningContentRepo.findAll(specification, Sort.by(Sort.Direction.fromString("asc"), "testField"))).thenReturn(successtalkList);
		learningContentDAO.fetchSuccesstalks("testField", "asc", filter, "testSearch");
	}
	
	@Test
	public void testFetchNewLearningContentWithoutFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.findNew()).thenReturn(newContentList);
		learningContentDAO.fetchNewLearningContent(filterParams);
	}
	
	@Test
	public void testFetchNewLearningContentWithFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("testkey", "testvalue");
		filterParams.put(Constants.SUCCESS_TRACK, Constants.CAMPUS_NETWORK);
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findAll()).thenReturn(newContentList);
		when(learningContentRepo.findNewFiltered(Mockito.any())).thenReturn(newContentList);
		learningContentDAO.fetchNewLearningContent(filterParams);
	}
	
	@Test
	public void testGetSuccessTalkCount() {
		when(learningContentRepo.countByLearningType(Constants.SUCCESSTALK)).thenReturn(1);
		learningContentDAO.getSuccessTalkCount();
	}
	
	@Test
	public void testGetPIWCount() {
		when(learningContentRepo.countByLearningType(Constants.PIW)).thenReturn(1);
		learningContentDAO.getPIWCount();
	}
	
	@Test
	public void testGetProductDocumentationCount() {
		when(learningContentRepo.countByLearningType(Constants.DOCUMENTATION)).thenReturn(1);
		learningContentDAO.getDocumentationCount();
	}
	
	@Test
	public void testGetViewMoreNewFiltersWithCount() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findNew()).thenReturn(newContentList);
		learningContentDAO.getViewMoreNewFiltersWithCount(filterParams, null);
	}
	
	@Test
	public void testGetViewMoreNewFiltersWithCountAndFilterAndNoCards() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findNew()).thenReturn(newContentList);
		learningContentDAO.getViewMoreNewFiltersWithCount(filterParams, filterCounts);
	}
	
	@Test
	public void testGetViewMoreNewFiltersWithCountAndFilter() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		Set<String> testCardIDs = new HashSet<>();
		testCardIDs.add("1");
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIDs);
		when(learningContentRepo.findNew()).thenReturn(newContentList);
		learningContentDAO.getViewMoreNewFiltersWithCount(filterParams, filterCounts);
	}
	
	@Test
	public void testFetchRecentlyViewedContentWithoutFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.getRecentlyViewedContent("101")).thenReturn(newContentList);
		learningContentDAO.fetchRecentlyViewedContent("101", filterParams);
	}
	
	@Test
	public void testFetchRecentlyViewedContentWithFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("testkey", "testvalue");
		filterParams.put(Constants.SUCCESS_TRACK, Constants.CAMPUS_NETWORK);
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findAll()).thenReturn(newContentList);
		when(learningContentRepo.getRecentlyViewedContentFiltered(Mockito.anyString(),Mockito.any())).thenReturn(newContentList);
		learningContentDAO.fetchRecentlyViewedContent("101",filterParams);
	}
	
	@Test
	public void testGetViewMoreRecentlyViewedFiltersWithCount() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.getRecentlyViewedContent("101")).thenReturn(newContentList);
		learningContentDAO.getRecentlyViewedFiltersWithCount("101","testuserid",filterParams, null);
	}
	
	@Test
	public void testGetViewMoreRecentlyViewedFiltersWithCountAndFilterAndNoCards() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.getRecentlyViewedContent("101")).thenReturn(newContentList);
		learningContentDAO.getRecentlyViewedFiltersWithCount("101","testuserid",filterParams, filterCounts);
	}
	
	@Test
	public void testGetViewMoreRecentlyViewedFiltersWithCountAndFilter() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		Set<String> testCardIDs = new HashSet<>();
		testCardIDs.add("1");
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIDs);
		when(learningContentRepo.getRecentlyViewedContent("101")).thenReturn(newContentList);
		learningContentDAO.getRecentlyViewedFiltersWithCount("101","testuserid",filterParams, filterCounts);
	}
	
	@Test
	public void testFetchFilteredContent() {
		learningContentDAO.fetchFilteredContent("101", "userid", new HashMap<String,String>());
	}
	
	@Test
	public void testFetchFilteredContentWithFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("testkey", "testvalue");
		filterParams.put(Constants.SUCCESS_TRACK, Constants.CAMPUS_NETWORK);
		learningContentDAO.fetchFilteredContent("101", "userid", filterParams);
	}
	
	@Test
	public void testGetBookmarkedFiltersWithCount() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<LearningContentItem> newContentList = new ArrayList<>();
		newContentList.add(getLearningItem());
		learningContentDAO.getBookmarkedFiltersWithCount(filterParams, null, newContentList);
	}
	
	@Test
	public void testGetBookmarkedFiltersWithCountAndFilterAndNoCards() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<LearningContentItem> newContentList = new ArrayList<>();
		newContentList.add(getLearningItem());
		learningContentDAO.getBookmarkedFiltersWithCount(filterParams, filterCounts, newContentList);
	}
	
	@Test
	public void testGetBookmarkedFiltersWithCountAndFilter() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<LearningContentItem> newContentList = new ArrayList<>();
		newContentList.add(getLearningItem());
		Set<String> testCardIDs = new HashSet<>();
		testCardIDs.add("1");
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIDs);
		learningContentDAO.getBookmarkedFiltersWithCount(filterParams, filterCounts, newContentList);
	}
	
	@Test
	public void testFetchUpcomingContentWithoutFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.findUpcoming()).thenReturn(newContentList);
		learningContentDAO.fetchUpcomingContent(filterParams);
	}
	
	@Test
	public void testFetchUpocmingContentWithFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("testkey", "testvalue");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findAll()).thenReturn(newContentList);
		when(learningContentRepo.findUpcomingFiltered(Mockito.any())).thenReturn(newContentList);
		learningContentDAO.fetchUpcomingContent(filterParams);
	}
	
	@Test
	public void testGeUpcomingFiltersWithCount() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findUpcoming()).thenReturn(newContentList);
		learningContentDAO.getUpcomingFiltersWithCount(filterParams, null);
	}
	
	@Test
	public void testGeUpcomingFiltersWithCountAndFilter() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findUpcoming()).thenReturn(newContentList);
		Set<String> testCardIDs = new HashSet<>();
		testCardIDs.add("1");
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIDs);
		learningContentDAO.getUpcomingFiltersWithCount(filterParams, filterCounts);
	}
	
	@Test
	public void testGeUpcomingFiltersWithCountAndFilterAndNoCards() {
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
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findUpcoming()).thenReturn(newContentList);
		learningContentDAO.getUpcomingFiltersWithCount(filterParams, filterCounts);
	}
	
	
	@Test
	public void testFetchSuccessAcademyContentWithoutFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		learningContentDAO.fetchSuccessAcademyContent(filterParams);
	}
	
	@Test
	public void testGetSuccessAcademyFiltersWithCount() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		learningContentDAO.getSuccessAcademyFiltersWithCount(filterParams, null);
	}
	
	@Test
	public void testGetSuccessAcademyFiltersWithCountAndFilterAndNoCards() {
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testRoleCount = new HashMap<>();
		testRoleCount.put("Renewals Manager", "1");
		filterCounts.put("Role" , testRoleCount);
		HashMap<String, String> testTechnologyCount = new HashMap<>();
		testTechnologyCount.put("Security", "1");
		filterCounts.put("Technology" , testTechnologyCount);
		HashMap<String, String> testModelCount = new HashMap<>();
		testModelCount.put("Operate", "1");
		filterCounts.put("Model" , testModelCount);
		HashMap<String, String> testSuccessTrackCount = new HashMap<>();
		testSuccessTrackCount.put("Campus", "1");
		filterCounts.put("Success Track" , testSuccessTrackCount);
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		learningContentDAO.getSuccessAcademyFiltersWithCount(filterParams, filterCounts);
	}
	
	@Test
	public void testGetSuccessAcademyFiltersWithCountAndFilter() {
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testRoleCount = new HashMap<>();
		testRoleCount.put("Renewals Manager", "1");
		filterCounts.put("Role" , testRoleCount);
		HashMap<String, String> testTechnologyCount = new HashMap<>();
		testTechnologyCount.put("Security", "1");
		filterCounts.put("Technology" , testTechnologyCount);
		HashMap<String, String> testModelCount = new HashMap<>();
		testModelCount.put("Operate", "1");
		filterCounts.put("Model" , testModelCount);
		HashMap<String, String> testSuccessTrackCount = new HashMap<>();
		testSuccessTrackCount.put("Campus", "1");
		filterCounts.put("Success Track" , testSuccessTrackCount);
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("language","English");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		Set<String> testCardIDs = new HashSet<>();
		testCardIDs.add("1");
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIDs);
		learningContentDAO.getSuccessAcademyFiltersWithCount(filterParams, filterCounts);
	}
	
	@Test
	public void testGetLearningMap() {
		String id= "testId";
		String title= "testTitle";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentRepo.findById(id)).thenReturn(Optional.of(getLearningEntity()));
		when(learningContentRepo.findByLearningTypeAndLearningMap(Constants.LEARNINGMODULE, getLearningEntity().getTitle())).thenReturn(learningEntityList);
		learningContentDAO.getLearningMap(id,title);
		
	}
	
	@Test
	public void testGetCXInsightsContent() {
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("testkey", "testvalue");
		filterParams.put(Constants.LFC_FILTER, "testvalue");
		filterParams.put(Constants.FOR_YOU_FILTER, "testvalue");
		String search = "testSearch";
		String sortField = "title";
		String sortType = "asc";
		List<String> learningItemIdsListCXInsights = new ArrayList<>();
		learningItemIdsListCXInsights.add("1");
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentRepo.getPitstopTaggedContentFilter(Mockito.any())).thenReturn(learningItemIdsListCXInsights);
		when(learningContentRepo.getPitstopTaggedContent()).thenReturn(learningItemIdsListCXInsights);
		when(learningContentRepo.getSortedByTitleAsc(Mockito.any())).thenReturn(learningEntityList);
		when(learningContentRepo.getSortedByTitleDesc(Mockito.any())).thenReturn(learningEntityList);
		learningContentDAO.fetchCXInsightsContent(filterParams, search, sortField, sortType);
		
	}
	
	@Test
	public void testGetCXInsightsContentWithoutLFCFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("testkey", "testvalue");
		filterParams.put(Constants.FOR_YOU_FILTER, "testvalue");
		String search = "testSearch";
		String sortField = "title";
		String sortType = "asc";
		List<String> learningItemIdsListCXInsights = new ArrayList<>();
		learningItemIdsListCXInsights.add("1");
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentRepo.getPitstopTaggedContentFilter(Mockito.any())).thenReturn(learningItemIdsListCXInsights);
		when(learningContentRepo.getPitstopTaggedContent()).thenReturn(learningItemIdsListCXInsights);
		when(learningContentRepo.getSortedByTitleAsc(Mockito.any())).thenReturn(learningEntityList);
		when(learningContentRepo.getSortedByTitleDesc(Mockito.any())).thenReturn(learningEntityList);
		learningContentDAO.fetchCXInsightsContent(filterParams, search, sortField, sortType);
		
	}
	
	NewLearningContentEntity getLearningEntity()
	{
		NewLearningContentEntity learning = new NewLearningContentEntity();
		learning.setId("testid");
		learning.setTitle("testTitle");
		learning.setSequence("1");
		return learning;
	}
	
	LearningContentItem getLearningItem()
	{
		LearningContentItem learning = new LearningContentItem(getLearningEntity());
		learning.setId("testid");
		return learning;
	}
}

