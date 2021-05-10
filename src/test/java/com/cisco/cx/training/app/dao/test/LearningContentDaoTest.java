package com.cisco.cx.training.app.dao.test;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.impl.NewLearningContentDAOImpl;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.LearningContentItem;


@RunWith(SpringRunner.class)
public class LearningContentDaoTest {

	@Mock
	private NewLearningContentRepo learningContentRepo;
	
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
		learningContentDAO.getViewMoreNewFiltersWithCount(filterParams, null, null);
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
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findNew()).thenReturn(newContentList);
		learningContentDAO.getViewMoreNewFiltersWithCount(filterParams, filterCounts, "test");
	}
	
	@Test
	public void testFetchRecentlyViewedContentWithoutFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.getRecentlyViewedContent("101","testuserid")).thenReturn(newContentList);
		learningContentDAO.fetchRecentlyViewedContent("101", "testuserid", filterParams);
	}
	
	@Test
	public void testFetchRecentlyViewedContentWithFilter() {
		Map<String, String> filterParams = new HashMap<String, String>();
		filterParams.put("testkey", "testvalue");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findAll()).thenReturn(newContentList);
		when(learningContentRepo.getRecentlyViewedContentFiltered(Mockito.anyString(), Mockito.anyString(),Mockito.any())).thenReturn(newContentList);
		learningContentDAO.fetchRecentlyViewedContent("101","testuserid",filterParams);
	}
	
	@Test
	public void testGetViewMoreRecentlyViewedFiltersWithCount() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.getRecentlyViewedContent("101","testuserid")).thenReturn(newContentList);
		learningContentDAO.getRecentlyViewedFiltersWithCount("101","testuserid",filterParams, null, null);
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
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.getRecentlyViewedContent("101","testuserid")).thenReturn(newContentList);
		learningContentDAO.getRecentlyViewedFiltersWithCount("101","testuserid",filterParams, filterCounts, "test");
	}
	
	@Test
	public void testFetchFilteredContent() {
		learningContentDAO.fetchFilteredContent("101", "userid", new HashMap<String,String>());
	}
	
	@Test
	public void testGetBookmarkedFiltersWithCount() {
		Map<String, String> filterParams = new HashMap<String, String>();
		List<LearningContentItem> newContentList = new ArrayList<>();
		newContentList.add(getLearningItem());
		learningContentDAO.getBookmarkedFiltersWithCount(filterParams, null, newContentList, null);
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
		List<LearningContentItem> newContentList = new ArrayList<>();
		newContentList.add(getLearningItem());
		learningContentDAO.getBookmarkedFiltersWithCount(filterParams, filterCounts, newContentList, "test");
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
		learningContentDAO.getUpcomingFiltersWithCount(filterParams, null, null);
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
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findUpcoming()).thenReturn(newContentList);
		learningContentDAO.getUpcomingFiltersWithCount(filterParams, filterCounts, "test");
	}
	
	NewLearningContentEntity getLearningEntity()
	{
		NewLearningContentEntity learning = new NewLearningContentEntity();
		learning.setId("testid");
		return learning;
	}
	
	LearningContentItem getLearningItem()
	{
		LearningContentItem learning = new LearningContentItem(getLearningEntity());
		learning.setId("testid");
		return learning;
	}
}

