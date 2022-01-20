package com.cisco.cx.training.app.dao.test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
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

	@Mock
	private LearningBookmarkDAO learningBookmarkDAO;

	@InjectMocks
	private NewLearningContentDAO learningContentDAO = new NewLearningContentDAOImpl();

	@Test
	public void testListPIWs() {
		List<NewLearningContentEntity> piwList = new ArrayList<>();
		LinkedHashMap<String, String> filter = new LinkedHashMap();
		filter.put("testKey", "testValue");
		Specification<NewLearningContentEntity> specification = new SpecificationBuilderPIW().filter(filter,
				"testSearch", "testRegion");
		when(learningContentRepo.findAll(specification, Sort.by(Sort.Direction.fromString("asc"), "testField")))
				.thenReturn(piwList);
		learningContentDAO.listPIWs("testRegion", "testField", "asc", filter, "testSearch");
	}

	@Test
	public void testFetchSuccesstalks() {
		List<NewLearningContentEntity> successtalkList = new ArrayList<>();
		LinkedHashMap<String, String> filter = new LinkedHashMap();
		filter.put("testKey", "testValue");
		Specification<NewLearningContentEntity> specification = new SpecificationBuilderPIW().filter(filter,
				"testSearch", "testRegion");
		when(learningContentRepo.findAll(specification, Sort.by(Sort.Direction.fromString("asc"), "testField")))
				.thenReturn(successtalkList);
		learningContentDAO.fetchSuccesstalks("testField", "asc", filter, "testSearch");
	}

	@Test
	public void testFetchNewLearningContentWithoutFilter() {
		Map<String, List<String>> filterParams = new HashMap<>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.findNew()).thenReturn(newContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchNewLearningContent(filterParams, stMapTest);
	}

	@Test
	public void testFetchNewLearningContentWithFilter() {
		Map<String, List<String>> filterParams = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		testValues.add("testvalue");
		testValues.add(Constants.CAMPUS_NETWORK);
		filterParams.put("testkey", testValues);
		filterParams.put(Constants.SUCCESS_TRACK, testValues);
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findAll()).thenReturn(newContentList);
		when(learningContentRepo.findNewFiltered(Mockito.any())).thenReturn(newContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchNewLearningContent(filterParams, stMapTest);
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
		HashMap<String, Object> filterSelected = new HashMap<>();
		learningContentDAO.getViewMoreNewFiltersWithCount(filterSelected);
	}

	@Test
	public void testGetViewMoreNewFiltersWithCountAndFilterAndNoCards() {
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		learningContentDAO.getViewMoreNewFiltersWithCount(filterSelected);
	}

	@Test
	public void testGetViewMoreNewFiltersWithCountAndFilterAndCards() {
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		Set<String> testCardIds = new HashSet<>();
		testCardIds.add("testString");
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
		learningContentDAO.getViewMoreNewFiltersWithCount(filterSelected);
	}

	@Test
	public void testGetViewMoreRecentlyFiltersWithCount() {
		String userId = "testUserId";
		HashMap<String, Object> filterSelected = new HashMap<>();
		learningContentDAO.getRecentlyViewedFiltersWithCount(userId, filterSelected);
	}

	@Test
	public void testGetViewMoreRecentlyFiltersWithCountAndNoCards() {
		String userId = "testUserId";
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		learningContentDAO.getRecentlyViewedFiltersWithCount(userId, filterSelected);
	}

	@Test
	public void testGetViewMoreRecentlyFiltersWithCountAndFilterAndCards() {
		String userId = "testUserId";
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		Set<String> testCardIds = new HashSet<>();
		testCardIds.add("testString");
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
		learningContentDAO.getRecentlyViewedFiltersWithCount(userId, filterSelected);
	}

	@Test
	public void testGetViewMoreUpcomingFiltersWithCount() {
		HashMap<String, Object> filterSelected = new HashMap<>();
		learningContentDAO.getUpcomingFiltersWithCount(filterSelected);
	}

	@Test
	public void testGetViewMoreUpcomingFiltersWithCountAndNoCards() {
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		learningContentDAO.getUpcomingFiltersWithCount(filterSelected);
	}

	@Test
	public void testGetViewMoreUpcomingFiltersWithCountAndFilterAndCards() {
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		Set<String> testCardIds = new HashSet<>();
		testCardIds.add("testString");
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
		learningContentDAO.getUpcomingFiltersWithCount(filterSelected);
	}

	@Test
	public void testGetViewMoreBookmarkedFiltersWithCount() {
		List<LearningContentItem> bookmarkedList = new ArrayList<>();
		bookmarkedList.add(getLearningItem());
		HashMap<String, Object> filterSelected = new HashMap<>();
		learningContentDAO.getBookmarkedFiltersWithCount(filterSelected, bookmarkedList);
	}

	@Test
	public void testGetViewMoreBookmarkedFiltersWithCountAndNoCards() {
		List<LearningContentItem> bookmarkedList = new ArrayList<>();
		bookmarkedList.add(getLearningItem());
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		learningContentDAO.getBookmarkedFiltersWithCount(filterSelected, bookmarkedList);
	}

	@Test
	public void testGetViewMoreBookmarkedFiltersWithCountAndFilterAndCards() {
		List<LearningContentItem> bookmarkedList = new ArrayList<>();
		bookmarkedList.add(getLearningItem());
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		Set<String> testCardIds = new HashSet<>();
		testCardIds.add("testString");
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
		learningContentDAO.getBookmarkedFiltersWithCount(filterSelected, bookmarkedList);
	}

	@Test
	public void testGetViewMoreCXInsightsFiltersWithCount() {
		String userId = "testUserId";
		String searchToken = "testSearchToken";
		HashMap<String, Object> filterSelected = new HashMap<>();
		learningContentDAO.getCXInsightsFiltersWithCount(userId, searchToken, filterSelected);
	}

	@Test
	public void testGetViewMoreCXInsightsFiltersWithCountAndNoCards() {
		String userId = "testUserId";
		String searchToken = "testSearchToken";
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		learningContentDAO.getCXInsightsFiltersWithCount(userId, searchToken, filterSelected);
	}

	@Test
	public void testGetViewMoreCXInsightsFiltersWithCountAndFilterAndCards() {
		String userId = "testUserId";
		String searchToken = "testSearchToken";
		HashMap<String, Object> filterSelected = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		Set<String> testCardIds = new HashSet<>();
		testCardIds.add("testString");
		testValues.add("testValue");
		filterSelected.put("testKey", testValues);
		when(filterCountsDao.andFilters(Mockito.any())).thenReturn(testCardIds);
		learningContentDAO.getCXInsightsFiltersWithCount(userId, searchToken, filterSelected);
	}

	@Test
	public void testFetchRecentlyViewedLearningContentWithoutFilter() {
		String userId = "testUserId";
		Map<String, List<String>> filterParams = new HashMap<>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.getRecentlyViewedContent(Mockito.anyString())).thenReturn(newContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchRecentlyViewedContent(userId, filterParams, stMapTest);
	}

	@Test
	public void testFetchRecentlyViewedLearningContentWithFilter() {
		String userId = "testUserId";
		Map<String, List<String>> filterParams = new HashMap<>();
		List<String> testValues = new ArrayList<>();
		List<String> testValuesForYouFilter = new ArrayList<>();
		testValuesForYouFilter.add(Constants.NEW);
		testValuesForYouFilter.add(Constants.BOOKMARKED_FOR_YOU);
		testValuesForYouFilter.add(Constants.RECENTLY_VIEWED);
		testValues.add("testvalue");
		testValues.add(Constants.CAMPUS_NETWORK);
		filterParams.put("testkey", testValues);
		filterParams.put(Constants.ROLE, testValues);
		filterParams.put(Constants.TECHNOLOGY, testValues);
		filterParams.put(Constants.LIFECYCLE, testValues);
		filterParams.put(Constants.FOR_YOU_FILTER, testValuesForYouFilter);
		filterParams.put(Constants.SUCCESS_TRACK, testValues);
		Set<String> testCardIds = new HashSet<>();
		testCardIds.add("testString");
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		newContentList.add(getLearningEntity());
		when(learningContentRepo.findAll()).thenReturn(newContentList);
		when(learningContentRepo.getRecentlyViewedContentFiltered(Mockito.anyString(), Mockito.any()))
				.thenReturn(newContentList);
		when(learningContentRepo.findNew()).thenReturn(newContentList);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(testCardIds);
		when(learningContentRepo.getRecentlyViewedContent(Mockito.anyString())).thenReturn(newContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchRecentlyViewedContent(userId, filterParams, stMapTest);
	}

	@Test
	public void testFetchCXInsightsContent() {
		String userId = "testUserId";
		String searchToken = "testSearchToken";
		String sortField = "title";
		String sortType = "asc";
		Map<String, List<String>> filterParams = new HashMap<>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.getRecentlyViewedContent(Mockito.anyString())).thenReturn(newContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchCXInsightsContent(userId, filterParams, stMapTest, searchToken, sortField, sortType);
	}

	@Test
	public void testFetchFilteredContent() {
		Map<String, List<String>> filterParams = new HashMap<>();
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchFilteredContent(filterParams, stMapTest);
	}

	@Test
	public void testUpcomingContent() {
		Map<String, List<String>> filterParams = new HashMap<>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		when(learningContentRepo.findUpcomingFiltered(Mockito.any())).thenReturn(newContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchUpcomingContent(filterParams, stMapTest);
	}

	@Test
	public void testFetchPopularAcrossPartnersContent() {
        ReflectionTestUtils.setField(learningContentDAO, "popularAcrossPartnersCategoryLiimit", Integer.valueOf(10));
		Map<String, List<String>> filterParams = new HashMap<>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		HashSet<String> bookmarks = new HashSet<String>();
		bookmarks.add("test");
		when(learningContentRepo.getPopularAcrossPartnersFiltered(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(newContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchPopularAcrossPartnersContent(filterParams, stMapTest, bookmarks);
	}

	@Test
	public void testGetPopularAcrossPartnersFiltersWithCount() {
        ReflectionTestUtils.setField(learningContentDAO, "popularAcrossPartnersCategoryLiimit", Integer.valueOf(10));
        HashSet<String> bookmarks = new HashSet<String>();
		bookmarks.add("test");
        HashMap<String, Object> filterSelected = new HashMap<>();
		learningContentDAO.getPopularAcrossPartnersFiltersWithCount(filterSelected, bookmarks);
	}

	@Test
	public void testFetchPopularAtPartnerContent() {
        ReflectionTestUtils.setField(learningContentDAO, "popularAtPartnerCompanyLimit", Integer.valueOf(10));
		Map<String, List<String>> filterParams = new HashMap<>();
		List<NewLearningContentEntity> newContentList = new ArrayList<>();
		HashSet<String> bookmarks = new HashSet<String>();
		bookmarks.add("test");
		Map stMapTest = new HashMap<>();
		Assertions.assertNotNull(learningContentDAO.fetchPopularAtPartnerContent(filterParams, stMapTest, "puid", bookmarks));
	}

	@Test
	public void testGetPopularAtPartnerFiltersWithCount() {
        ReflectionTestUtils.setField(learningContentDAO, "popularAtPartnerCompanyLimit", Integer.valueOf(10));
		HashMap<String, Object> filterSelected = new HashMap<>();
		Assertions.assertNotNull(learningContentDAO.getPopularAtPartnerFiltersWithCount(filterSelected, "test", new HashSet<String>()));
	}

	@Test
	public void testGetLearningMap() {
		String id = "testId";
		String title = "testTitle";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentRepo.findById(id)).thenReturn(Optional.of(getLearningEntity()));
		when(learningContentRepo.findByLearningTypeAndLearningMap(Constants.LEARNINGMODULE,
				getLearningEntity().getTitle())).thenReturn(learningEntityList);
		learningContentDAO.getLearningMap(id, title);

	}

	@Test
	public void testGetLearningMapIDNull() {
		String title = "testTitle";
		List<NewLearningContentEntity> learningEntityList = new ArrayList<>();
		learningEntityList.add(getLearningEntity());
		when(learningContentRepo.findByLearningTypeAndLearningMap(Constants.LEARNINGMODULE,
				getLearningEntity().getTitle())).thenReturn(learningEntityList);
		learningContentDAO.getLearningMap(null, title);

	}

	@Test
	public void testGetSuccessTrackCount() {
		when(learningContentRepo.getSuccessTracksCount()).thenReturn(1);
		learningContentDAO.getSuccessTracksCount();
	}

	@Test
	public void testGetLifecycleCount() {
		when(learningContentRepo.getLifecycleCount()).thenReturn(1);
		learningContentDAO.getLifecycleCount();
	}

	@Test
	public void testGetTechnologyCount() {
		when(learningContentRepo.getTechnologyount()).thenReturn(1);
		learningContentDAO.getTechnologyCount();
	}

	@Test
	public void testGetRoleCount() {
		when(learningContentRepo.getRolesCount()).thenReturn(1);
		learningContentDAO.getRolesCount();
	}
	
	@Test
	public void testFetchFeaturedContent() {
		Map<String, List<String>> filterParams = new HashMap<>();
		List<NewLearningContentEntity> featuredContentList = new ArrayList<>();
		when(learningContentRepo.findFeatured()).thenReturn(featuredContentList);
		Map stMapTest = new HashMap<>();
		learningContentDAO.fetchFeaturedContent(filterParams, stMapTest);
	}
	
	@Test
	public void testFetchFeaturedContentWithoutFilter() {
		List<NewLearningContentEntity> featuredContentList = new ArrayList<>();
		when(learningContentRepo.findFeatured()).thenReturn(featuredContentList);
		learningContentDAO.fetchFeaturedContent(Collections.emptyMap(), null);
	}
	
	@Test
	public void testGetFeaturedFiltersWithoutFilter() {
		HashMap<String, Object> filterSelected = new HashMap<>();
		learningContentDAO.getFeaturedFiltersWithCount(filterSelected);
	}
	
	@Test
	public void testGetFeaturedFiltersWithCount() {
		HashMap<String, Object> filterSelected = new HashMap<>();
		filterSelected.put("Content Type", "PDF");
		learningContentDAO.getFeaturedFiltersWithCount(filterSelected);
	}


	NewLearningContentEntity getLearningEntity() {
		NewLearningContentEntity learning = new NewLearningContentEntity();
		learning.setId("testid");
		learning.setTitle("testTitle");
		learning.setSequence("1");
		return learning;
	}

	LearningContentItem getLearningItem() {
		LearningContentItem learning = new LearningContentItem(getLearningEntity());
		learning.setId("testid");
		return learning;
	}
}
