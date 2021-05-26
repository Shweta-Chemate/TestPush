package com.cisco.cx.training.app.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.builders.SpecificationBuilder;
import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.builders.SpecificationBuilderSuccessTalk;
import com.cisco.cx.training.app.dao.FilterCountsDAO;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CustomSpecifications;
import com.cisco.cx.training.models.LearningContentItem;

@Repository
public class FilterCountsDAOImpl implements FilterCountsDAO{

	private final static Logger LOG = LoggerFactory.getLogger(FilterCountsDAOImpl.class);

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	public static final HashMap<String, String> filterGroupMappings=getMappings();

	private static HashMap<String, String> getMappings() {
		HashMap<String, String> filterGroupMappings=new HashMap<String, String>();
		filterGroupMappings.put(Constants.LANGUAGE_PRM,Constants.LANGUAGE);
		filterGroupMappings.put(Constants.REGION, Constants.LIVE_EVENTS);
		filterGroupMappings.put(Constants.CONTENT_TYPE_PRM, Constants.CONTENT_TYPE);
		filterGroupMappings.put(Constants.ROLE, Constants.ROLE);
		filterGroupMappings.put(Constants.MODEL, Constants.MODEL);
		filterGroupMappings.put(Constants.TECHNOLOGY, Constants.TECHNOLOGY);
		filterGroupMappings.put(Constants.DOCUMENTATION_FILTER_PRM, Constants.DOCUMENTATION_FILTER);
		filterGroupMappings.put(Constants.SUCCESS_TRACK, Constants.SUCCESS_TRACK);
		return filterGroupMappings;
	}

	@Override
	public Set<String> andFilters(Map<String, Set<String>> filteredCards)
	{
		Set<String> cardIds =  new HashSet<String>();

		/** AND **/
		if(!filteredCards.isEmpty())
		{
			String[] keys = filteredCards.keySet().toArray(new String[0]);
			for(int i=0; i<keys.length; i++)
			{
				if(i==0) cardIds.addAll(filteredCards.get(keys[i]));
				else cardIds.retainAll(filteredCards.get(keys[i]));
			}
		}
		LOG.info("mapped = {} ",cardIds);

		return cardIds;
	}

	@Override
	public void setFilterCounts(Set<String> cardIdsInp, HashMap<String, HashMap<String, String>> filterCountsMap,
			Map<String, Set<String>> filteredCardsMap) {
		if(filteredCardsMap ==null || filteredCardsMap.isEmpty() || filteredCardsMap.size()==1) 
		{
			setFilterCounts(cardIdsInp, filterCountsMap);
			return;
		}
		if(filterCountsMap.containsKey(Constants.CONTENT_TYPE)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.CONTENT_TYPE);
			List<Map<String,Object>> dbListCT = learningContentRepo.getAllContentTypeWithCountByCards(cardIds);
			filterCountsMap.get(Constants.CONTENT_TYPE).putAll(listToMap(dbListCT));
		}

		if(filterCountsMap.containsKey(Constants.LANGUAGE)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.LANGUAGE);
			List<Map<String,Object>> dbListLang = learningContentRepo.getAllLanguagesWithCountByCards(cardIds);
			filterCountsMap.get(Constants.LANGUAGE).putAll(listToMap(dbListLang));
		}

		if(filterCountsMap.containsKey(Constants.LIVE_EVENTS)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.LIVE_EVENTS);
			List<Map<String,Object>> dbListReg = learningContentRepo.getAllRegionsWithCountByCards(cardIds);
			filterCountsMap.get(Constants.LIVE_EVENTS).putAll(listToMap(dbListReg));
		}

		if(filterCountsMap.containsKey(Constants.ROLE)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.ROLE);
			List<Map<String,Object>> dbListRole = learningContentRepo
					.findSuccessAcademyFiltered(Constants.ROLE, cardIds);
			filterCountsMap.get(Constants.ROLE).putAll(listToMap(dbListRole));
		}

		if(filterCountsMap.containsKey(Constants.MODEL)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.MODEL);
			List<Map<String,Object>> dbListModel = learningContentRepo
					.findSuccessAcademyFiltered(Constants.MODEL, cardIds);
			filterCountsMap.get(Constants.MODEL).putAll(listToMap(dbListModel));
		}

		if(filterCountsMap.containsKey(Constants.TECHNOLOGY)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.TECHNOLOGY);
			List<Map<String,Object>> dbListTech = learningContentRepo
					.findSuccessAcademyFiltered(Constants.TECHNOLOGY, cardIds);
			filterCountsMap.get(Constants.TECHNOLOGY).putAll(listToMap(dbListTech));
		}

		if(filterCountsMap.containsKey(Constants.DOCUMENTATION_FILTER)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.DOCUMENTATION_FILTER);
			List<Map<String,Object>> dbListDoc = learningContentRepo
					.getDocFilterCountByCards(cardIds);
			filterCountsMap.get(Constants.DOCUMENTATION_FILTER).putAll(listToMap(dbListDoc));
		}

		if(filterCountsMap.containsKey(Constants.SUCCESS_TRACK)) {
			Set<String> cardIds = andFiltersWithExcludeKey(filteredCardsMap,Constants.SUCCESS_TRACK);
			List<Map<String,Object>> dbListST = learningContentRepo
					.getAllStWithCountByCards(cardIds);
			Map<String,String> dbListSTFinal=listToMap(dbListST);
			int SACount=learningContentRepo.getSACampusCount(cardIds);
			if(SACount>0) {
				if(dbListSTFinal.containsKey(Constants.CAMPUS_NETWORK)) {
					dbListSTFinal.put(Constants.CAMPUS_NETWORK,
							Integer.toString((Integer.valueOf(dbListSTFinal.get(Constants.CAMPUS_NETWORK))+SACount)));
				}
				else
					dbListSTFinal.put(Constants.CAMPUS_NETWORK, Integer.toString(SACount));
			}
			filterCountsMap.get(Constants.SUCCESS_TRACK).putAll(dbListSTFinal);
		}
	}

	@Override
	public void setFilterCounts(Set<String> cardIds, HashMap<String, HashMap<String, String>> filterCountsMap) {

		if(filterCountsMap.containsKey(Constants.CONTENT_TYPE)) {
			List<Map<String,Object>> dbListCT = learningContentRepo.getAllContentTypeWithCountByCards(cardIds);
			filterCountsMap.get(Constants.CONTENT_TYPE).putAll(listToMap(dbListCT));
		}

		if(filterCountsMap.containsKey(Constants.LANGUAGE)) {
			List<Map<String,Object>> dbListLang = learningContentRepo.getAllLanguagesWithCountByCards(cardIds);
			filterCountsMap.get(Constants.LANGUAGE).putAll(listToMap(dbListLang));
		}

		if(filterCountsMap.containsKey(Constants.LIVE_EVENTS)) {
			List<Map<String,Object>> dbListReg = learningContentRepo.getAllRegionsWithCountByCards(cardIds);
			filterCountsMap.get(Constants.LIVE_EVENTS).putAll(listToMap(dbListReg));
		}

		if(filterCountsMap.containsKey(Constants.ROLE)) {
			List<Map<String,Object>> dbListRole = learningContentRepo
					.findSuccessAcademyFiltered(Constants.ROLE, cardIds);
			filterCountsMap.get(Constants.ROLE).putAll(listToMap(dbListRole));
		}

		if(filterCountsMap.containsKey(Constants.MODEL)) {
			List<Map<String,Object>> dbListModel = learningContentRepo
					.findSuccessAcademyFiltered(Constants.MODEL, cardIds);
			filterCountsMap.get(Constants.MODEL).putAll(listToMap(dbListModel));
		}

		if(filterCountsMap.containsKey(Constants.TECHNOLOGY)) {
			List<Map<String,Object>> dbListTech = learningContentRepo
					.findSuccessAcademyFiltered(Constants.TECHNOLOGY, cardIds);
			filterCountsMap.get(Constants.TECHNOLOGY).putAll(listToMap(dbListTech));
		}

		if(filterCountsMap.containsKey(Constants.DOCUMENTATION_FILTER)) {
			List<Map<String,Object>> dbListDoc = learningContentRepo
					.getDocFilterCountByCards(cardIds);
			filterCountsMap.get(Constants.DOCUMENTATION_FILTER).putAll(listToMap(dbListDoc));
		}

		if (filterCountsMap.containsKey(Constants.SUCCESS_TRACK)) {
			List<Map<String, Object>> dbListST = learningContentRepo.getAllStWithCountByCards(cardIds);
			Map<String, String> dbListSTFinal = listToMap(dbListST);
			int SACount = learningContentRepo.getSACampusCount(cardIds);
			if (SACount > 0) {
				if (dbListSTFinal.containsKey(Constants.CAMPUS_NETWORK)) {
					dbListSTFinal.put(Constants.CAMPUS_NETWORK,
							Integer.toString((Integer.valueOf(dbListSTFinal.get(Constants.CAMPUS_NETWORK)) + SACount)));
				} else
					dbListSTFinal.put(Constants.CAMPUS_NETWORK, Integer.toString(SACount));
			}
			filterCountsMap.get(Constants.SUCCESS_TRACK).putAll(dbListSTFinal);

		}
	}

	private Set<String> andFiltersWithExcludeKey(Map<String, Set<String>> filteredCardsMap, String excludeKey) {
		Set<String> cardIds = new HashSet<String>();

		/** AND **/
		if (!filteredCardsMap.isEmpty()) {
			String[] keys = filteredCardsMap.keySet().toArray(new String[0]);
			int first = -1;
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].equalsIgnoreCase(excludeKey))
					continue;
				if (first == -1)
					first = i;
				if (i == first)
					cardIds.addAll(filteredCardsMap.get(keys[i]));
				else
					cardIds.retainAll(filteredCardsMap.get(keys[i]));
			}
		}
		return cardIds;
	}

	@Override
	public Map<String, Set<String>> filterCards(Map<String, String> filter, Set<String> learningItemIdsList) {
		Map<String, Set<String>> filteredCards = new HashMap<String, Set<String>>();
		String[] keys=filter.keySet().toArray(new String[0]);
		for (int i=0;i<keys.length;i++) {
			String filterGroup=keys[i];
			String value=filter.get(filterGroup);
			List<String> values = new ArrayList<String>(Arrays.asList(value.split(",")));
			if(filterGroup.contains("assetFacet")) {
				filter.remove(filterGroup);
				filterGroup=learningContentRepo.getAssetModelByValue(values.get(0));
				filter.put(filterGroup, value);
			}
			LOG.info("filter={}",filterGroup);
			switch(filterGroup) {
			case Constants.CONTENT_TYPE_PRM : filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByCT(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.LANGUAGE_PRM : filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByLang(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.REGION : filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByReg(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.ROLE : filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByfacet(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.MODEL : filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByfacet(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.TECHNOLOGY : filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByfacet(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.SUCCESS_TRACK : {
				if(values.contains(Constants.CAMPUS_NETWORK))
					values.add(Constants.CAMPUS);
				filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByST(new HashSet<String>(values),learningItemIdsList));break;
			}
			case Constants.DOCUMENTATION_FILTER_PRM : filteredCards.put(FilterCountsDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByDoc(new HashSet<String>(values),learningItemIdsList));break;
			default : LOG.info("other {}={}",filterGroup,values);
			}
		}
		return filteredCards;
	}

	@Override
	public void initializeFiltersWithCounts(List<String> filterGroups, HashMap<String, HashMap<String, String>> countFilters, Set<String> learningItemIdsList) {		

		if(filterGroups.contains(Constants.CONTENT_TYPE)) {
			HashMap<String, String> contentTypeFilter = new HashMap<>();
			List<Map<String,Object>> dbListCT = learningContentRepo
					.getAllContentTypeWithCountByCards(learningItemIdsList);
			Map<String,String> allContentsCT = listToMap(dbListCT);
			contentTypeFilter.putAll(allContentsCT);
			if(!contentTypeFilter.isEmpty()) {
				countFilters.put(Constants.CONTENT_TYPE, contentTypeFilter);
			}
		}

		if(filterGroups.contains(Constants.LANGUAGE)) {
			HashMap<String, String> languageFilter = new HashMap<>();
			List<Map<String,Object>> dbListLang =  learningContentRepo
					.getAllLanguagesWithCountByCards(learningItemIdsList);
			Map<String,String> allContentsLANG = listToMap(dbListLang);
			languageFilter.putAll(allContentsLANG);
			if(!languageFilter.isEmpty()) {
				countFilters.put(Constants.LANGUAGE, languageFilter);
			}
		}

		if(filterGroups.contains(Constants.LIVE_EVENTS)) {
			HashMap<String, String> regionFilter = new HashMap<>();
			List<Map<String,Object>> dbListReg =  learningContentRepo
					.getAllRegionsWithCountByCards(learningItemIdsList);
			Map<String,String> allContentsReg = listToMap(dbListReg);
			regionFilter.putAll(allContentsReg);
			if(!regionFilter.isEmpty()) {
				countFilters.put(Constants.LIVE_EVENTS, regionFilter);
			}
		}

		if(filterGroups.contains(Constants.ROLE)) {
			HashMap<String, String> roleFilter = new HashMap<>();
			List<Map<String,Object>> dbListRole =  learningContentRepo
					.findSuccessAcademyFiltered(Constants.ROLE, learningItemIdsList);
			Map<String,String> allContentsRole = listToMap(dbListRole);
			roleFilter.putAll(allContentsRole);
			if(!roleFilter.isEmpty()) {
				countFilters.put(Constants.ROLE, roleFilter);
			}
		}

		if(filterGroups.contains(Constants.ROLE)) {
			HashMap<String, String> modelFilter = new HashMap<>();
			List<Map<String,Object>> dbListModel =   learningContentRepo
					.findSuccessAcademyFiltered(Constants.MODEL, learningItemIdsList);
			Map<String,String> allContentsModel = listToMap(dbListModel);
			modelFilter.putAll(allContentsModel);
			if(!modelFilter.isEmpty()) {
				countFilters.put(Constants.MODEL, modelFilter);
			}
		}

		if(filterGroups.contains(Constants.ROLE)) {
			HashMap<String, String> techFilter = new HashMap<>();
			List<Map<String,Object>> dbListTech =   learningContentRepo
					.findSuccessAcademyFiltered(Constants.TECHNOLOGY, learningItemIdsList);
			Map<String,String> allContentsTech = listToMap(dbListTech);
			techFilter.putAll(allContentsTech);
			if(!techFilter.isEmpty()) {
				countFilters.put(Constants.TECHNOLOGY, techFilter);
			}
		}

		if(filterGroups.contains(Constants.SUCCESS_TRACK)) {
			HashMap<String, String> STFilter = new HashMap<>();
			List<Map<String,Object>> dbListST =  learningContentRepo
					.getAllStWithCountByCards(learningItemIdsList);
			Map<String,String> allContentsST = listToMap(dbListST);
			int SACount=learningContentRepo.getSACampusCount(learningItemIdsList);
			if(SACount>0) {
				if(allContentsST.containsKey(Constants.CAMPUS_NETWORK)) {
					allContentsST.put(Constants.CAMPUS_NETWORK,
							Integer.toString((Integer.valueOf(allContentsST.get(Constants.CAMPUS_NETWORK))+SACount)));
				}
				else
					allContentsST.put(Constants.CAMPUS_NETWORK, Integer.toString(SACount));
			}
			STFilter.putAll(allContentsST);
			if(!STFilter.isEmpty()) {
				countFilters.put(Constants.SUCCESS_TRACK, STFilter);
			}
		}

		if(filterGroups.contains(Constants.DOCUMENTATION_FILTER)) {
			HashMap<String, String> docFilter = new HashMap<>();
			List<Map<String,Object>> dbListDoc =   learningContentRepo
					.getDocFilterCountByCards(learningItemIdsList);
			Map<String,String> allContentsDoc = listToMap(dbListDoc);
			docFilter.putAll(allContentsDoc);
			if(!docFilter.isEmpty()) {
				countFilters.put(Constants.DOCUMENTATION_FILTER, docFilter);
			}
		}
	}

	private Map<String,String> listToMap(List<Map<String,Object>> dbList)
	{
		Map<String,String> countMap = new HashMap<String,String>();
		for(Map<String,Object> dbMap : dbList)
		{
			String dbKey = String.valueOf(dbMap.get("label"));
			String dbValue = String.valueOf(dbMap.get("count"));			
			countMap.put(dbKey,dbValue);		
		}
		return countMap;
	}

}
