package com.cisco.cx.training.app.dao.test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.app.dao.FilterCountsDAO;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.dao.impl.FilterCountsDAOImpl;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;

@ExtendWith(SpringExtension.class)
public class FilterCountsDaoTest {

	@Mock
	private NewLearningContentRepo learningContentRepo;

	@Mock
	private LearningBookmarkDAO learningBookmarkDAO;

	@InjectMocks
	private FilterCountsDAO filterCountsDao = new FilterCountsDAOImpl();

	@Test
	public void testAndFilters() {
		Map<String, Set<String>> filteredCards = new HashMap<>();
		Set<String> testSet1 = new HashSet<>();
		Set<String> testSet2 = new HashSet<>();
		testSet1.add("testvalue1");
		testSet2.add("testvalue2");
		filteredCards.put("testkey1", testSet1);
		filteredCards.put("testkey2", testSet2);
		filterCountsDao.andFilters(filteredCards);
	}

	@Test
	public void testSetFilterCounts() {
		HashMap<String, Object> filterCountsMap = new HashMap<>();
		Map testValues = new HashMap<>();
		testValues.put("testValueKey", "testValue");
		filterCountsMap.put("testKey", testValues);
		Set<String> cardIds = new HashSet<>();
		cardIds.add("testString");
		String filterGroup = "testFilterGroup";
		String userId = "testUserId";
		filterCountsMap.put(Constants.CONTENT_TYPE, testValues);
		filterCountsMap.put(Constants.LANGUAGE, testValues);
		filterCountsMap.put(Constants.LIVE_EVENTS, testValues);
		filterCountsMap.put(Constants.ROLE, testValues);
		filterCountsMap.put(Constants.TECHNOLOGY, testValues);
		filterCountsMap.put(Constants.DOCUMENTATION_FILTER, testValues);
		filterCountsMap.put(Constants.SUCCESS_TRACK, testValues);
		filterCountsMap.put(Constants.LIFECYCLE, testValues);
		filterCountsMap.put(Constants.FOR_YOU_FILTER, testValues);
		List<Map<String, Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRoleCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllTechCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLFCWithCountByCards((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.getAllStUcWithCount((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.findNewFilteredIds(Mockito.any())).thenReturn(cardIds);
		when(learningContentRepo.getRecentlyViewedContentFilteredIds(Mockito.anyString(), Mockito.any()))
				.thenReturn(cardIds);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(cardIds);
		filterCountsDao.setFilterCounts(cardIds, filterCountsMap, filterGroup, userId);
	}

	@Test
	public void testSetFilterCountsWithFilteredMapMultiple() {
		HashMap<String, Object> filterCountsMap = new HashMap<>();
		Map testValues = new HashMap<>();
		testValues.put("testValueKey", "testValue");
		filterCountsMap.put("testKey", testValues);
		Set<String> cardIds = new HashSet<>();
		cardIds.add("testString");
		String filterGroup = "testFilterGroup";
		String userId = "testUserId";
		filterCountsMap.put(Constants.CONTENT_TYPE, testValues);
		filterCountsMap.put(Constants.LANGUAGE, testValues);
		filterCountsMap.put(Constants.LIVE_EVENTS, testValues);
		filterCountsMap.put(Constants.ROLE, testValues);
		filterCountsMap.put(Constants.TECHNOLOGY, testValues);
		filterCountsMap.put(Constants.DOCUMENTATION_FILTER, testValues);
		filterCountsMap.put(Constants.SUCCESS_TRACK, testValues);
		filterCountsMap.put(Constants.LIFECYCLE, testValues);
		filterCountsMap.put(Constants.FOR_YOU_FILTER, testValues);
		Map<String, Set<String>> filteredCardsMap = new HashMap<>();
		filteredCardsMap.put("testKey1", cardIds);
		filteredCardsMap.put("testKey2", cardIds);
		List<Map<String, Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRoleCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllTechCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLFCWithCountByCards((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.getAllStUcWithCount((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.findNewFilteredIds(Mockito.any())).thenReturn(cardIds);
		when(learningContentRepo.getRecentlyViewedContentFilteredIds(Mockito.anyString(), Mockito.any()))
				.thenReturn(cardIds);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(cardIds);
		filterCountsDao.setFilterCounts(cardIds, filterCountsMap, filteredCardsMap, userId);
	}

	@Test
	public void testSetFilterCountsWithFilteredMap() {
		HashMap<String, Object> filterCountsMap = new HashMap<>();
		Map testValues = new HashMap<>();
		testValues.put("testValueKey", "testValue");
		filterCountsMap.put("testKey", testValues);
		Set<String> cardIds = new HashSet<>();
		cardIds.add("testString");
		String filterGroup = "testFilterGroup";
		String userId = "testUserId";
		filterCountsMap.put(Constants.CONTENT_TYPE, testValues);
		filterCountsMap.put(Constants.LANGUAGE, testValues);
		filterCountsMap.put(Constants.LIVE_EVENTS, testValues);
		filterCountsMap.put(Constants.ROLE, testValues);
		filterCountsMap.put(Constants.TECHNOLOGY, testValues);
		filterCountsMap.put(Constants.DOCUMENTATION_FILTER, testValues);
		filterCountsMap.put(Constants.SUCCESS_TRACK, testValues);
		filterCountsMap.put(Constants.LIFECYCLE, testValues);
		filterCountsMap.put(Constants.FOR_YOU_FILTER, testValues);
		Map<String, Set<String>> filteredCardsMap = new HashMap<>();
		filteredCardsMap.put("testKey", cardIds);
		List<Map<String, Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRoleCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllTechCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLFCWithCountByCards((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.getAllStUcWithCount((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.findNewFilteredIds(Mockito.any())).thenReturn(cardIds);
		when(learningContentRepo.getRecentlyViewedContentFilteredIds(Mockito.anyString(), Mockito.any()))
				.thenReturn(cardIds);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(cardIds);
		filterCountsDao.setFilterCounts(cardIds, filterCountsMap, filteredCardsMap, userId);
	}

	@Test
	public void testFilterCards() {
		Map<String, Object> filtersSelected = new HashMap<>();
		Set<String> learningItemIdsList = new HashSet<>();
		learningItemIdsList.add("testId");
		String userId = "testUserId";
		List<String> testValues = new ArrayList<>();
		testValues.add("testValue");
		List<String> testValuesForYouFilter = new ArrayList<>();
		testValuesForYouFilter.add(Constants.NEW);
		testValuesForYouFilter.add(Constants.RECENTLY_VIEWED);
		testValuesForYouFilter.add(Constants.BOOKMARKED_FOR_YOU);
		filtersSelected.put("testKey", testValues);
		filtersSelected.put(Constants.CONTENT_TYPE, testValues);
		filtersSelected.put(Constants.LANGUAGE, testValues);
		filtersSelected.put(Constants.LIVE_EVENTS, testValues);
		filtersSelected.put(Constants.ROLE, testValues);
		filtersSelected.put(Constants.TECHNOLOGY, testValues);
		filtersSelected.put(Constants.DOCUMENTATION_FILTER, testValues);
		filtersSelected.put(Constants.SUCCESS_TRACK, testValues);
		filtersSelected.put(Constants.LIFECYCLE, testValues);
		filtersSelected.put(Constants.FOR_YOU_FILTER, testValuesForYouFilter);
		Map stMap = new HashMap<>();
		Map pitstopMap = new HashMap<>();
		stMap.put("testKeyMao", pitstopMap);
		filtersSelected.put(Constants.SUCCESS_TRACK, stMap);
		List<Map<String, Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRoleCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllTechCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLFCWithCountByCards((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.getAllStUcWithCount((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.findNewFilteredIds(Mockito.any())).thenReturn(learningItemIdsList);
		when(learningContentRepo.getRecentlyViewedContentFilteredIds(Mockito.anyString(), Mockito.any()))
				.thenReturn(learningItemIdsList);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(learningItemIdsList);

		filterCountsDao.filterCards(filtersSelected, learningItemIdsList, userId);
	}

	@Test
	public void testInitializeFiltersWithCounts() {
		List<String> filterGroups = new ArrayList<>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();
		Set<String> learningItemIdsList = new HashSet<>();
		String userId = "testUserId";
		learningItemIdsList.add("testId");
		List<String> testValues = new ArrayList<>();
		testValues.add("testValue");
		List<String> testValuesForYouFilter = new ArrayList<>();
		testValuesForYouFilter.add(Constants.NEW);
		testValuesForYouFilter.add(Constants.RECENTLY_VIEWED);
		testValuesForYouFilter.add(Constants.BOOKMARKED_FOR_YOU);
		filterGroups.add(Constants.CONTENT_TYPE);
		filterGroups.add(Constants.LANGUAGE);
		filterGroups.add(Constants.LIVE_EVENTS);
		filterGroups.add(Constants.ROLE);
		filterGroups.add(Constants.TECHNOLOGY);
		filterGroups.add(Constants.DOCUMENTATION_FILTER);
		filterGroups.add(Constants.SUCCESS_TRACK);
		filterGroups.add(Constants.LIFECYCLE);
		filterGroups.add(Constants.FOR_YOU_FILTER);
		filterGroups.add(Constants.SUCCESS_TRACK);
		List<Map<String, Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRoleCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllTechCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLFCWithCountByCards((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.getAllStUcWithCount((Mockito.any()))).thenReturn(dbList);
		when(learningContentRepo.findNewFilteredIds(Mockito.any())).thenReturn(learningItemIdsList);
		when(learningContentRepo.getRecentlyViewedContentFilteredIds(Mockito.anyString(), Mockito.any()))
				.thenReturn(learningItemIdsList);
		when(learningBookmarkDAO.getBookmarks(Mockito.anyString())).thenReturn(learningItemIdsList);
		filterCountsDao.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, userId);
	}
}
