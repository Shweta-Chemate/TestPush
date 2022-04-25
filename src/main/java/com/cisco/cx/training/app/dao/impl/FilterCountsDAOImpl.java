package com.cisco.cx.training.app.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.cisco.cx.training.app.dao.FilterCountsDAO;
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.constants.Constants;

@SuppressWarnings({"squid:S134","java:S3776"})
@Repository  
public class FilterCountsDAOImpl implements FilterCountsDAO{

	private static final Logger LOG = LoggerFactory.getLogger(FilterCountsDAOImpl.class);

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Autowired
	private LearningBookmarkDAO learningBookmarkDAO;

	@Override
	public Set<String> andFilters(Map<String, Set<String>> filteredCards)
	{
		Set<String> cardIds =  new HashSet<>();

		/** AND **/
		if(!filteredCards.isEmpty())
		{
			String[] keys = filteredCards.keySet().toArray(new String[0]);
			for(int i=0; i<keys.length; i++)
			{
				if(i==0) { cardIds.addAll(filteredCards.get(keys[i])); }
				else { cardIds.retainAll(filteredCards.get(keys[i])); }
			}
		}
		LOG.info("mapped = {} ",cardIds);

		return cardIds;
	}

	/*
	 * set filter counts for each filter  when only one filter group is selected
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setFilterCounts(Set<String> cardIds, HashMap<String, Object> filterCountsMap, String filterGroup, String userId) {
		if(filterCountsMap.containsKey(Constants.CONTENT_TYPE) && !filterGroup.equals(Constants.CONTENT_TYPE)) {
			List<Map<String,Object>> dbListCT = learningContentRepo.getAllContentTypeWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.CONTENT_TYPE)).putAll(listToMap(dbListCT));
		}

		if(filterCountsMap.containsKey(Constants.LANGUAGE) && !filterGroup.equals(Constants.LANGUAGE)) {
			List<Map<String,Object>> dbListLang = learningContentRepo.getAllLanguagesWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.LANGUAGE)).putAll(listToMap(dbListLang));
		}

		if(filterCountsMap.containsKey(Constants.LIVE_EVENTS) && !filterGroup.equals(Constants.LIVE_EVENTS)) {
			List<Map<String,Object>> dbListReg = learningContentRepo.getAllRegionsWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.LIVE_EVENTS)).putAll(listToMap(dbListReg));
		}

		if(filterCountsMap.containsKey(Constants.ROLE) && !filterGroup.equals(Constants.ROLE)) {
			List<Map<String,Object>> dbListRole = learningContentRepo
					.getAllRoleCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.ROLE)).putAll(listToMap(dbListRole));
		}

		if(filterCountsMap.containsKey(Constants.TECHNOLOGY) && !filterGroup.equals(Constants.TECHNOLOGY)) {
			List<Map<String,Object>> dbListTech = learningContentRepo
					.getAllTechCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.TECHNOLOGY)).putAll(listToMap(dbListTech));
		}

		if(filterCountsMap.containsKey(Constants.LIFECYCLE) && !filterGroup.equals(Constants.LIFECYCLE)) {
			List<Map<String,Object>> dbListLFC = learningContentRepo
					.getAllLFCWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.LIFECYCLE)).putAll(listToMap(dbListLFC));
		}

		if(filterCountsMap.containsKey(Constants.SUCCESS_TRACK) && !filterGroup.equals(Constants.SUCCESS_TRACK))
		{
			List<Map<String,Object>> dbListST = learningContentRepo.getAllStUcWithCount(cardIds);
			Map<String,Object> filterAndCountsFromDb = listToSTMap(dbListST,null);
			mergeSTFilterCounts(filterCountsMap,filterAndCountsFromDb);
		}

		if(filterCountsMap.containsKey(Constants.FOR_YOU_FILTER) && !filterGroup.equals(Constants.FOR_YOU_FILTER)) {
			Map<String, String> forYouMap=new TreeMap<>();
			int count = learningContentRepo.getRecentlyViewedContentFilteredIds(userId, cardIds).size();
			if(count>0) {
				forYouMap.put(Constants.RECENTLY_VIEWED, Integer.toString(count));}
			Set<String> bookmarkIds=getBookMarkedIds(userId);
			bookmarkIds.retainAll(cardIds);
			count = bookmarkIds.size();
			if(count>0) {
				forYouMap.put(Constants.BOOKMARKED_FOR_YOU, Integer.toString(count));}
			((Map<String, String>) filterCountsMap.get(Constants.FOR_YOU_FILTER)).putAll(forYouMap);
		}
	}

	/*
	 * set filter counts for each filter groups
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setFilterCounts(Set<String> cardIdsInp, HashMap<String, Object> filterCountsMap,
			Map<String, Set<String>> filteredCardsMap, String userId) {
		if(filteredCardsMap.size()==1)
		{
			setFilterCounts(cardIdsInp, filterCountsMap, (String)filteredCardsMap.keySet().toArray()[0], userId);
			return;
		}
		if(filterCountsMap.containsKey(Constants.CONTENT_TYPE)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.CONTENT_TYPE);
			List<Map<String,Object>> dbListCT = learningContentRepo.getAllContentTypeWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.CONTENT_TYPE)).putAll(listToMap(dbListCT));
		}

		if(filterCountsMap.containsKey(Constants.LANGUAGE)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.LANGUAGE);
			List<Map<String,Object>> dbListLang = learningContentRepo.getAllLanguagesWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.LANGUAGE)).putAll(listToMap(dbListLang));
		}

		if(filterCountsMap.containsKey(Constants.LIVE_EVENTS)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.LIVE_EVENTS);
			List<Map<String,Object>> dbListReg = learningContentRepo.getAllRegionsWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.LIVE_EVENTS)).putAll(listToMap(dbListReg));
		}

		if(filterCountsMap.containsKey(Constants.ROLE)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.ROLE);
			List<Map<String,Object>> dbListRole = learningContentRepo
					.getAllRoleCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.ROLE)).putAll(listToMap(dbListRole));
		}

		if(filterCountsMap.containsKey(Constants.TECHNOLOGY)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.TECHNOLOGY);
			List<Map<String,Object>> dbListTech = learningContentRepo
					.getAllTechCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.TECHNOLOGY)).putAll(listToMap(dbListTech));
		}

		if(filterCountsMap.containsKey(Constants.LIFECYCLE)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.LIFECYCLE);
			List<Map<String,Object>> dbListLFC = learningContentRepo
					.getAllLFCWithCountByCards(cardIds);
			((Map<String, String>) filterCountsMap.get(Constants.LIFECYCLE)).putAll(listToMap(dbListLFC));
		}

		if(filterCountsMap.containsKey(Constants.SUCCESS_TRACK))
		{
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap, Constants.SUCCESS_TRACK);
			List<Map<String,Object>> dbListST = learningContentRepo.getAllStUcWithCount(cardIds);
			Map<String,Object> filterAndCountsFromDb = listToSTMap(dbListST,null);
			mergeSTFilterCounts(filterCountsMap,filterAndCountsFromDb);
		}

		if(filterCountsMap.containsKey(Constants.FOR_YOU_FILTER)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.FOR_YOU_FILTER);
			Map<String, String> forYouMap=new TreeMap<>();
			int count = learningContentRepo.getRecentlyViewedContentFilteredIds(userId, cardIds).size();
			if(count>0) {
				forYouMap.put(Constants.RECENTLY_VIEWED, Integer.toString(count));}
			Set<String> bookmarkIds=getBookMarkedIds(userId);
			bookmarkIds.retainAll(cardIds);
			count = bookmarkIds.size();
			if(count>0) {
				forYouMap.put(Constants.BOOKMARKED_FOR_YOU, Integer.toString(count));}
			((Map<String, String>) filterCountsMap.get(Constants.FOR_YOU_FILTER)).putAll(forYouMap);

		}
	}

	/*
	 * get learning items ids pertaining to all the filter groups selected and ignoring the excludeKey filter group
	 */
	private Set<String> andFiltersWithExcludeKey(Map<String, Set<String>> filteredCardsMap, String excludeKey) {
		Set<String> cardIds = new HashSet<>();

		/** AND **/
		if (!filteredCardsMap.isEmpty()) {
			String[] keys = filteredCardsMap.keySet().toArray(new String[0]);
			int first = -1;
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].equalsIgnoreCase(excludeKey)) {
					continue;}
				if (first == -1) {
					first = i;}
				if (i == first) {
					cardIds.addAll(filteredCardsMap.get(keys[i]));}
				else {
					cardIds.retainAll(filteredCardsMap.get(keys[i]));}
			}
		}
		return cardIds;
	}

	/*
	 * store all learning items ids that falls under each filter group that is selected 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Set<String>> filterCards(Map<String, Object> filtersSelected, Set<String> learningItemIdsList, String userId){
		LOG.info("applyFilters = {}",filtersSelected);
		Map<String, Set<String>> filteredCards = new HashMap<>();
		if(filtersSelected==null || filtersSelected.isEmpty()) {return filteredCards;}

		/** OR **/
		filtersSelected.keySet().forEach(filterGroup -> {
			Object v = filtersSelected.get(filterGroup);
			List<String> list;
			if(v instanceof List) {
				list= (List<String>)v;
				switch(filterGroup) {
				case Constants.CONTENT_TYPE : filteredCards.put(filterGroup, learningContentRepo.getCardIdsByCT(new HashSet<>(list),learningItemIdsList));break;
				case Constants.LANGUAGE : filteredCards.put(filterGroup, learningContentRepo.getCardIdsByLang(new HashSet<>(list),learningItemIdsList));break;
				case Constants.LIVE_EVENTS : filteredCards.put(filterGroup, learningContentRepo.getCardIdsByReg(new HashSet<>(list),learningItemIdsList));break;
				case Constants.ROLE : filteredCards.put(filterGroup, learningContentRepo.getCardIdsByRole(new HashSet<>(list),learningItemIdsList));break;
				case Constants.TECHNOLOGY : filteredCards.put(filterGroup, learningContentRepo.getCardIdsByTech(new HashSet<>(list),learningItemIdsList));break;
				case Constants.LIFECYCLE : filteredCards.put(filterGroup, learningContentRepo.getCardIdsByLFC(new HashSet<>(list),learningItemIdsList));break;
				case Constants.FOR_YOU_FILTER : {Set<String> cardIds=new HashSet<>();
					if(list.contains(Constants.RECENTLY_VIEWED)) {cardIds.addAll(learningContentRepo.getRecentlyViewedContentFilteredIds(userId, learningItemIdsList));}
					if(list.contains(Constants.BOOKMARKED_FOR_YOU)){Set<String> bookmarkIds=getBookMarkedIds(userId);bookmarkIds.retainAll(learningItemIdsList);cardIds.addAll(bookmarkIds);}
					filteredCards.put(filterGroup, cardIds);} break;
				default : LOG.info("other {}={}",filterGroup,list);
				}
			}
			else if ( v instanceof Map) {
				Set<String> cardIdsStUc = new HashSet<>();
				//LOG.info("ST="+((Map) v).keySet()); //NOSONAR
				((Map) v).keySet().forEach(ik->{
					Object iv = ((Map)v).get(ik);
					if(iv instanceof Map) {
						//LOG.info("UC="+((Map) iv).keySet()); //NOSONAR
						Set<String> usecases= ((Map) iv).keySet();
						String successtrack = ik.toString();
						cardIdsStUc.addAll(learningContentRepo.getCardIdsByUcStFilter(successtrack, usecases, learningItemIdsList));
					}
				});
				filteredCards.put(filterGroup, cardIdsStUc);
			}
		});
		LOG.info("filteredCards = {} ",filteredCards);
		return filteredCards;
	}

	/*
	 * initialize filters with count. This is done to get the initial hierarchy of filters. 
	 */
	@Override
	public void initializeFiltersWithCounts(List<String> filterGroups, HashMap<String, Object> filters, HashMap<String, Object> countFilters, Set<String> learningItemIdsList, String userId) {

		if(filterGroups.contains(Constants.CONTENT_TYPE)) {
			Map<String, String> contentTypeFilter = new LinkedHashMap<>();
			List<Map<String,Object>> dbListCT = learningContentRepo
					.getAllContentTypeWithCountByCards(learningItemIdsList);
			Map<String,String> allContentsCT = listToMap(dbListCT);
			if(!allContentsCT.isEmpty()) {
				countFilters.put(Constants.CONTENT_TYPE, allContentsCT);
				filters.put(Constants.CONTENT_TYPE, contentTypeFilter);
				allContentsCT.keySet().forEach(k -> contentTypeFilter.put(k, "0"));
			}
		}

		if(filterGroups.contains(Constants.LANGUAGE)) {
			Map<String, String> languageFilter = new LinkedHashMap<>();
			List<Map<String,Object>> dbListLang =  learningContentRepo
					.getAllLanguagesWithCountByCards(learningItemIdsList);
			Map<String,String> allContentsLANG = listToMap(dbListLang);
			if(!allContentsLANG.isEmpty()) {
				countFilters.put(Constants.LANGUAGE, allContentsLANG);
				filters.put(Constants.LANGUAGE, languageFilter);
				allContentsLANG.keySet().forEach(k -> languageFilter.put(k, "0"));
			}
		}

		if(filterGroups.contains(Constants.LIVE_EVENTS)) {
			Map<String, String> regionFilter = new LinkedHashMap<>();
			List<Map<String,Object>> dbListReg =  learningContentRepo
					.getAllRegionsWithCountByCards(learningItemIdsList);
			Map<String,String> allContentsReg = listToMap(dbListReg);
			if(!allContentsReg.isEmpty()) {
				countFilters.put(Constants.LIVE_EVENTS, allContentsReg);
				filters.put(Constants.LIVE_EVENTS, regionFilter);
				allContentsReg.keySet().forEach(k -> regionFilter.put(k, "0"));
			}
		}

		if(filterGroups.contains(Constants.ROLE)) {
			Map<String, String> roleFilter = new LinkedHashMap<>();
			List<Map<String,Object>> dbListRole =  learningContentRepo
					.getAllRoleCountByCards(learningItemIdsList);
			Map<String,String> allContentsRole = listToMap(dbListRole);
			if(!allContentsRole.isEmpty()) {
				countFilters.put(Constants.ROLE, allContentsRole);
				filters.put(Constants.ROLE, roleFilter);
				allContentsRole.keySet().forEach(k -> roleFilter.put(k, "0"));
			}
		}

		if(filterGroups.contains(Constants.TECHNOLOGY)) {
			Map<String, String> techFilter = new LinkedHashMap<>();
			List<Map<String,Object>> dbListTech =   learningContentRepo
					.getAllTechCountByCards(learningItemIdsList);
			Map<String,String> allContentsTech = listToMap(dbListTech);
			if(!allContentsTech.isEmpty()) {
				countFilters.put(Constants.TECHNOLOGY, allContentsTech);
				filters.put(Constants.TECHNOLOGY, techFilter);
				allContentsTech.keySet().forEach(k -> techFilter.put(k, "0"));
			}
		}

		if(filterGroups.contains(Constants.SUCCESS_TRACK)) {
			Map<String, Object> stFilter = new LinkedHashMap<>();
			List<Map<String,Object>> dbListST = learningContentRepo.getAllStUcWithCount(learningItemIdsList);
			Map<String,Object> allContentsST = listToSTMap(dbListST,stFilter);
			if(!stFilter.isEmpty()) {
				countFilters.put(Constants.SUCCESS_TRACK, allContentsST);
				filters.put(Constants.SUCCESS_TRACK, stFilter);
			}
		}

		if(filterGroups.contains(Constants.LIFECYCLE)) {
			initializeLCWithCounts(filters, countFilters, learningItemIdsList);
		}

		if(filterGroups.contains(Constants.FOR_YOU_FILTER)) {
			initializeYouWithCounts(filters, countFilters, learningItemIdsList, userId);
		}
	}
	
	private void initializeLCWithCounts(HashMap<String, Object> filters, 
	HashMap<String, Object> countFilters, Set<String> learningItemIdsList) {
		Map<String, String> lfcFilter = new LinkedHashMap<>();
		List<Map<String,Object>> dbListLFC = learningContentRepo.getAllLFCWithCountByCards(learningItemIdsList);
		Map<String,String> allContentsLFC = listToMap(dbListLFC);
		if(!allContentsLFC.isEmpty()) {
			countFilters.put(Constants.LIFECYCLE, allContentsLFC);
			filters.put(Constants.LIFECYCLE, lfcFilter);
			allContentsLFC.keySet().forEach(k -> lfcFilter.put(k, "0"));
		}
	
	}
	
	private void initializeYouWithCounts(HashMap<String, Object> filters, 
	HashMap<String, Object> countFilters, Set<String> learningItemIdsList, String userId) {
		Map<String, String> forYouMap=new LinkedHashMap<>();
		Map<String, String> forYouMapEmpty=new LinkedHashMap<>();
		Set<String> bookmarkIds=getBookMarkedIds(userId);
		bookmarkIds.retainAll(learningItemIdsList);
		int count = bookmarkIds.size();
		if(count>0) {
			forYouMap.put(Constants.BOOKMARKED_FOR_YOU, Integer.toString(count));
			forYouMapEmpty.put(Constants.BOOKMARKED_FOR_YOU, "0");
		}
		count = learningContentRepo.getRecentlyViewedContentFilteredIds(userId, learningItemIdsList).size();
		if(count>0) {
			forYouMap.put(Constants.RECENTLY_VIEWED, Integer.toString(count));
			forYouMapEmpty.put(Constants.RECENTLY_VIEWED, "0");
		}
		if(!forYouMap.isEmpty()) {
			countFilters.put(Constants.FOR_YOU_FILTER, forYouMap);
			filters.put(Constants.FOR_YOU_FILTER, forYouMapEmpty);
		}
	}

	private Map<String,String> listToMap(List<Map<String,Object>> dbList)
	{
		Map<String,String> countMap = new TreeMap<>();
		for(Map<String,Object> dbMap : dbList)
		{
			String dbKey = String.valueOf(dbMap.get("label"));
			String dbValue = String.valueOf(dbMap.get("count"));			
			countMap.put(dbKey,dbValue);		
		}
		return countMap;
	}

	private Map<String,Object> listToSTMap(List<Map<String,Object>> dbList, final Map<String,Object> stFilter)
	{
		Map<String,Object> stAllKeysMap = new HashMap<>();
		Map<String,Object> stCountMap = new HashMap<>();

		Map<String,Object> stMap = new HashMap<>();

		Set<String> distinctST = new HashSet<>();
		Map<String,List<String>> distinctUCForST = new HashMap<>();
		Map<String,List<String>> distinctPSForUC = new HashMap<>();

		for(Map<String,Object> dbMap : dbList)
		{
			String st = String.valueOf(dbMap.get("successtrack"));
			String uc = String.valueOf(dbMap.get("usecase"));
			String ps = String.valueOf(dbMap.get("pitstop"));

			String dbValue = String.valueOf(dbMap.get("dbvalue"));

			distinctST.add(st);
			if(!distinctUCForST.keySet().contains(st)) { distinctUCForST.put(st, new ArrayList<>());}
			distinctUCForST.get(st).add(uc);
			if(!distinctPSForUC.keySet().contains(uc)) {distinctPSForUC.put(uc, new ArrayList<>());}
			distinctPSForUC.get(uc).add(ps);

			if(!stMap.keySet().contains(st)) {stMap.put(st, new HashMap<String,Map<String,String>>()) ;}
			if(!((Map)stMap.get(st)).keySet().contains(uc)) {((Map)stMap.get(st)).put(uc, dbValue);}//.put(uc, new HashMap<String,String>()); //NOSONAR
			//if(!((Map)((Map)stMap.get(st)).get(uc)).keySet().contains(ps)) ((Map)((Map)stMap.get(st)).get(uc)).put(ps, dbValue); //NOSONAR

			if(stFilter!=null)
			{
				if(!stAllKeysMap.keySet().contains(st)) { stAllKeysMap.put(st, new HashMap<>()) ;}
				if(!((Map)stAllKeysMap.get(st)).keySet().contains(uc)) {((Map)stAllKeysMap.get(st)).put(uc, "0");}//.put(uc, new HashMap<String,String>());//NOSONAR
				//if(!((Map)((Map)stAllKeysMap.get(st)).get(uc)).keySet().contains(ps)) ((Map)((Map)stAllKeysMap.get(st)).get(uc)).put(ps, "0"); //NOSONAR
			}
		}
		stCountMap.putAll(stMap);if(stFilter!=null) {stFilter.putAll(stAllKeysMap);}
		LOG.info("stCountMap {} , stFilter={}",stCountMap, stFilter);
		return stCountMap;
	}

	@SuppressWarnings("unchecked")
	private void mergeSTFilterCounts(Map<String,Object> filters , Map<String,Object> filterAndCountsFromDb) {
		Map<String,Object> stFilters = ((Map<String,Object>)filters.get(Constants.SUCCESS_TRACK));
		for(Entry<String, Object> entry : filterAndCountsFromDb.entrySet()) {
			String stkey= entry.getKey();
			if(stFilters.containsKey(stkey)) {
				Map<String,Object> stFilter = (Map<String,Object>)stFilters.get(stkey);
				Map<String,Object> stFilterFromDB = (Map<String,Object>)entry.getValue();
				for(Entry<String, Object> useCaseEntry : stFilterFromDB.entrySet()) {
					String useCaseKey = useCaseEntry.getKey();
					stFilter.computeIfPresent(useCaseKey, (key, value)->
						useCaseEntry.getValue());
				}
			}
		}
	}

	Set<String> getBookMarkedIds(String userId){
		return learningBookmarkDAO.getBookmarks(userId);
	}


}
