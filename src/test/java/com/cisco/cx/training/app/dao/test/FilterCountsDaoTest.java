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
import com.cisco.cx.training.app.dao.impl.FilterCountsDAOImpl;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;

@ExtendWith(SpringExtension.class)
public class FilterCountsDaoTest {
	
	@Mock
	private NewLearningContentRepo learningContentRepo;
	
	@InjectMocks
	private FilterCountsDAO filterCountsDao = new FilterCountsDAOImpl();
	
	@Test
	public void testInitializeFiltersWithCountsNoFilter() {
		List<String> filterGroup = new ArrayList<>();
		filterGroup.add(Constants.CONTENT_TYPE);
		filterGroup.add(Constants.LANGUAGE);
		filterGroup.add(Constants.LIVE_EVENTS);
		filterGroup.add(Constants.ROLE);
		filterGroup.add(Constants.SUCCESS_TRACK);
		filterGroup.add(Constants.DOCUMENTATION_FILTER);
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		List<Map<String,Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.findSuccessAcademyFiltered(Mockito.anyString(), Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getSACampusCount(Mockito.any())).thenReturn(1);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		filterCountsDao.initializeFiltersWithCounts(filterGroup, countFilters, learningItemIdsList);
	}
	
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
		Set<String> cardIds = new HashSet<>();
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testMap = new HashMap<>();
		testMap.put("testKey", "testValue");
		testMap.put(Constants.CAMPUS_NETWORK, "testValue");
		filterCounts.put(Constants.LIVE_EVENTS , testMap);
		filterCounts.put(Constants.CONTENT_TYPE , testMap);
		filterCounts.put(Constants.LANGUAGE , testMap);
		filterCounts.put(Constants.ROLE, testMap);
		filterCounts.put(Constants.SUCCESS_TRACK, testMap);
		filterCounts.put(Constants.DOCUMENTATION_FILTER, testMap);
		filterCounts.put(Constants.MODEL, testMap);
		filterCounts.put(Constants.TECHNOLOGY, testMap);
		List<Map<String,Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.findSuccessAcademyFiltered(Mockito.anyString(), Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getSACampusCount(Mockito.any())).thenReturn(1);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllStWithCountByCards(Mockito.any())).thenReturn(dbList);
		filterCountsDao.setFilterCounts(cardIds, filterCounts);
	}

	@Test
	public void testSetFilterCountsEmptyMap() {
		Set<String> cardIds = new HashSet<>();
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		HashMap<String, String> testMap = new HashMap<>();
		testMap.put("testKey", "testValue");
		testMap.put(Constants.CAMPUS_NETWORK, "testValue");
		filterCountsDao.setFilterCounts(cardIds, filterCounts, null);
	}
	
	@Test
	public void testSetFilterCountsWithFilterMap() {
		Set<String> cardIds = new HashSet<>();
		HashMap<String, HashMap<String, String>> filterCounts = new HashMap<>();
		Map<String, Set<String>> filteredCards = new HashMap<>();
		Set<String> testSet1 = new HashSet<>();
		Set<String> testSet2 = new HashSet<>();
		testSet1.add("testvalue1");
		testSet2.add("testvalue2");
		filteredCards.put("testkey1", testSet1);
		filteredCards.put("testkey2", testSet2);
		HashMap<String, String> testMap = new HashMap<>();
		testMap.put("testKey", "testValue");
		testMap.put(Constants.CAMPUS_NETWORK, "testValue");
		filterCounts.put(Constants.LIVE_EVENTS , testMap);
		filterCounts.put(Constants.CONTENT_TYPE , testMap);
		filterCounts.put(Constants.LANGUAGE , testMap);
		filterCounts.put(Constants.ROLE, testMap);
		filterCounts.put(Constants.SUCCESS_TRACK, testMap);
		filterCounts.put(Constants.DOCUMENTATION_FILTER, testMap);
		filterCounts.put(Constants.MODEL, testMap);
		filterCounts.put(Constants.TECHNOLOGY, testMap);
		List<Map<String,Object>> dbList = new ArrayList<>();
		when(learningContentRepo.getAllContentTypeWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllLanguagesWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllRegionsWithCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.findSuccessAcademyFiltered(Mockito.anyString(), Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getSACampusCount(Mockito.any())).thenReturn(1);
		when(learningContentRepo.getDocFilterCountByCards(Mockito.any())).thenReturn(dbList);
		when(learningContentRepo.getAllStWithCountByCards(Mockito.any())).thenReturn(dbList);
		filterCountsDao.setFilterCounts(cardIds, filterCounts, filteredCards);
	}
	
	@Test
	public void testFilterCards() {
		Map<String, String> filter = new HashMap<>();
		String testValue = "testValue";
		filter.put(Constants.CONTENT_TYPE_PRM , testValue);
		filter.put(Constants.LANGUAGE_PRM , testValue);
		filter.put(Constants.REGION , testValue);
		filter.put(Constants.ROLE, testValue);
		filter.put(Constants.SUCCESS_TRACK, Constants.CAMPUS_NETWORK);
		filter.put(Constants.DOCUMENTATION_FILTER_PRM, testValue);
		filter.put(Constants.MODEL, testValue);
		filter.put(Constants.TECHNOLOGY, testValue);
		filter.put(Constants.ASSET_FACET, testValue);
		Set<String> learningItemIdsList = new HashSet<>();
		when(learningContentRepo.getAssetModelByValue(Mockito.any())).thenReturn(testValue);
		when(learningContentRepo.getCardIdsByCT(Mockito.any(), Mockito.any())).thenReturn(learningItemIdsList);
		when(learningContentRepo.getCardIdsByLang(Mockito.any(), Mockito.any())).thenReturn(learningItemIdsList);
		when(learningContentRepo.getCardIdsByReg(Mockito.any(), Mockito.any())).thenReturn(learningItemIdsList);
		when(learningContentRepo.getCardIdsByfacet(Mockito.any(), Mockito.any())).thenReturn(learningItemIdsList);
		when(learningContentRepo.getCardIdsByST(Mockito.any(), Mockito.any())).thenReturn(learningItemIdsList);
		when(learningContentRepo.getCardIdsByDoc(Mockito.any(), Mockito.any())).thenReturn(learningItemIdsList);
		filterCountsDao.filterCards(filter, learningItemIdsList);
	}
}
