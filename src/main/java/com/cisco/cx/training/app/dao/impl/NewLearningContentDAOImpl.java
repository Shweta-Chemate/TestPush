package com.cisco.cx.training.app.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.builders.SpecificationBuilder;
import com.cisco.cx.training.app.builders.SpecificationBuilderPIW;
import com.cisco.cx.training.app.builders.SpecificationBuilderSuccessTalk;
import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;
import com.cisco.cx.training.constants.Constants;
import com.cisco.cx.training.models.CustomSpecifications;
import com.cisco.cx.training.models.LearningContentItem;

@Repository
public class NewLearningContentDAOImpl implements NewLearningContentDAO{

	private final static Logger LOG = LoggerFactory.getLogger(NewLearningContentDAOImpl.class);

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	private static final HashMap<String, String> filterGroupMappings=getMappings();

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
	public List<NewLearningContentEntity> fetchNewLearningContent(Map<String, String> filterParams) {
		List<NewLearningContentEntity> result;
		List<NewLearningContentEntity> learningContentListSACampus = new ArrayList<>();
		Set<String> productDocSuccesstrackfilters=getSuccessTrackFilters(filterParams);
		if(filterParams.isEmpty())
			result= learningContentRepo.findNew();
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			specification = specification.and(new SpecificationBuilder().filter(filterParams));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=learningContentRepo.findNewFiltered(learningItemIdsList);
		}
		//filter content w.r.t product documentation successtrack filters
		if(!productDocSuccesstrackfilters.isEmpty()) {
			Set<String> learningContentIds=result.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			if(productDocSuccesstrackfilters.contains(Constants.CAMPUS_NETWORK)) {
				learningContentListSACampus=result.stream()
						.filter(entity->entity.getAssetFacet()!=null && entity.getAssetFacet().equals(Constants.CAMPUS)).collect(Collectors.toList());
			}
			result=learningContentRepo.getCardsBySt(productDocSuccesstrackfilters, learningContentIds);
			result.addAll(learningContentListSACampus);
		}
		return result;
	}
	
	@Override
	public List<NewLearningContentEntity> fetchSuccesstalks(String sortField, String sortType,
			Map<String, String> filterParams, String search) {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilderSuccessTalk().filter(filterParams, search));
		return learningContentRepo.findAll(specification,Sort.by(Sort.Direction.fromString(sortType),sortField));
	}

	@Override
	public List<NewLearningContentEntity> listPIWs(String region, String sortField, String sortType,
			Map<String, String> filterParams, String search) {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilderPIW().filter(filterParams, search, region));
		return learningContentRepo.findAll(specification,Sort.by(Sort.Direction.fromString(sortType),sortField));
	}

	@Override
	public Integer getSuccessTalkCount() {
		return learningContentRepo.countByLearningType(Constants.SUCCESSTALK);
	}

	@Override
	public Integer getPIWCount() {
		return learningContentRepo.countByLearningType(Constants.PIW);
	}

	@Override
	public Integer getDocumentationCount() {
		return learningContentRepo.countByLearningType(Constants.DOCUMENTATION);
	}
	
	@Override
	public HashMap<String, HashMap<String,String>> getViewMoreNewFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCountsMap) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		filteredList = fetchNewLearningContent(new HashMap<String,String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		initializeFiltersWithCounts(filters,countFilters,learningItemIdsList);


		if(filter.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCards(filter, learningItemIdsList);
			cardIds = andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(NewLearningContentDAOImpl.filterGroupMappings.get(k),
						countFilters.get(NewLearningContentDAOImpl.filterGroupMappings.get(k))));
			}
			setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}

	}

	@Override
	public HashMap<String, HashMap<String, String>> getRecentlyViewedFiltersWithCount(String puid, String userId, Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCountsMap) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		filteredList = fetchRecentlyViewedContent(userId, new HashMap<String,String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		initializeFiltersWithCounts(filters,countFilters,learningItemIdsList);

		if(filter.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCards(filter, learningItemIdsList);
			cardIds = andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(NewLearningContentDAOImpl.filterGroupMappings.get(k),
						countFilters.get(NewLearningContentDAOImpl.filterGroupMappings.get(k))));
			}
			setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
	}

	@Override
	public HashMap<String, HashMap<String, String>> getUpcomingFiltersWithCount(Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		filteredList = fetchUpcomingContent(new HashMap<String,String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		initializeFiltersWithCounts(filters,countFilters,learningItemIdsList);

		if(filter.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCards(filter, learningItemIdsList);
			cardIds = andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(NewLearningContentDAOImpl.filterGroupMappings.get(k),
						countFilters.get(NewLearningContentDAOImpl.filterGroupMappings.get(k))));
			}
			setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
	}

	@Override
	public HashMap<String, HashMap<String, String>> getBookmarkedFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String, String>> filterCounts,
			List<LearningContentItem> filteredBookmarkedList) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		learningItemIdsList = filteredBookmarkedList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		initializeFiltersWithCounts(filters,countFilters,learningItemIdsList);

		if(filter.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCards(filter, learningItemIdsList);
			cardIds = andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(NewLearningContentDAOImpl.filterGroupMappings.get(k),
						countFilters.get(NewLearningContentDAOImpl.filterGroupMappings.get(k))));
			}
			setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
	}

	@Override
	public HashMap<String, HashMap<String, String>> getSuccessAcademyFiltersWithCount(Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		filteredList = fetchSuccessAcademyContent(new HashMap<String, String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		initializeFiltersWithCounts(filters,countFilters,learningItemIdsList);

		if(filter.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCards(filter, learningItemIdsList);
			cardIds = andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(NewLearningContentDAOImpl.filterGroupMappings.get(k),
						countFilters.get(NewLearningContentDAOImpl.filterGroupMappings.get(k))));
			}
			setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
	}

	private Set<String> andFilters(Map<String, Set<String>> filteredCards)
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

	private void setFilterCounts(Set<String> cardIdsInp, HashMap<String, HashMap<String, String>> filterCountsMap,
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

	private void setFilterCounts(Set<String> cardIds, HashMap<String, HashMap<String, String>> filterCountsMap) {

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

	private Map<String, Set<String>> filterCards(Map<String, String> filter, Set<String> learningItemIdsList) {
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
			case Constants.CONTENT_TYPE_PRM : filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByCT(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.LANGUAGE_PRM : filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByLang(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.REGION : filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByReg(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.ROLE : filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByfacet(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.MODEL : filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByfacet(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.TECHNOLOGY : filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByfacet(new HashSet<String>(values),learningItemIdsList));break;
			case Constants.SUCCESS_TRACK : {
				if(values.contains(Constants.CAMPUS_NETWORK))
					values.add(Constants.CAMPUS);
				filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByST(new HashSet<String>(values),learningItemIdsList));break;
			}
			case Constants.DOCUMENTATION_FILTER_PRM : filteredCards.put(NewLearningContentDAOImpl.filterGroupMappings.get(filterGroup), learningContentRepo.getCardIdsByDoc(new HashSet<String>(values),learningItemIdsList));break;
			default : LOG.info("other {}={}",filterGroup,values);
			}
		}
		return filteredCards;
	}

	private void initializeFiltersWithCounts(HashMap<String, HashMap<String, String>> filters,HashMap<String, HashMap<String, String>> countFilters, Set<String> learningItemIdsList) {
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		List<Map<String,Object>> dbListCT = learningContentRepo
				.getAllContentTypeWithCountByCards(learningItemIdsList);
		Map<String,String> allContentsCT = listToMap(dbListCT);
		contentTypeFilter.putAll(allContentsCT);
		if(!contentTypeFilter.isEmpty()) {
			countFilters.put(Constants.CONTENT_TYPE, contentTypeFilter);
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.CONTENT_TYPE, emptyFilter);
			allContentsCT.keySet().forEach(k -> emptyFilter.put(k, "0"));
		}

		HashMap<String, String> languageFilter = new HashMap<>();
		List<Map<String,Object>> dbListLang =  learningContentRepo
				.getAllLanguagesWithCountByCards(learningItemIdsList);
		Map<String,String> allContentsLANG = listToMap(dbListLang);
		languageFilter.putAll(allContentsLANG);
		if(!languageFilter.isEmpty()) {
			countFilters.put(Constants.LANGUAGE, languageFilter);
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.LANGUAGE, emptyFilter);
			allContentsLANG.keySet().forEach(k -> emptyFilter.put(k, "0"));
		}

		HashMap<String, String> regionFilter = new HashMap<>();
		List<Map<String,Object>> dbListReg =  learningContentRepo
				.getAllRegionsWithCountByCards(learningItemIdsList);
		Map<String,String> allContentsReg = listToMap(dbListReg);
		regionFilter.putAll(allContentsReg);
		if(!regionFilter.isEmpty()) {
			countFilters.put(Constants.LIVE_EVENTS, regionFilter);
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.LIVE_EVENTS, emptyFilter);
			allContentsReg.keySet().forEach(k -> emptyFilter.put(k, "0"));
		}

		HashMap<String, String> roleFilter = new HashMap<>();
		List<Map<String,Object>> dbListRole =  learningContentRepo
				.findSuccessAcademyFiltered(Constants.ROLE, learningItemIdsList);
		Map<String,String> allContentsRole = listToMap(dbListRole);
		roleFilter.putAll(allContentsRole);
		if(!roleFilter.isEmpty()) {
			countFilters.put(Constants.ROLE, roleFilter);
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.ROLE, emptyFilter);
			allContentsRole.keySet().forEach(k -> emptyFilter.put(k, "0"));
		}

		HashMap<String, String> modelFilter = new HashMap<>();
		List<Map<String,Object>> dbListModel =   learningContentRepo
				.findSuccessAcademyFiltered(Constants.MODEL, learningItemIdsList);
		Map<String,String> allContentsModel = listToMap(dbListModel);
		modelFilter.putAll(allContentsModel);
		if(!modelFilter.isEmpty()) {
			countFilters.put(Constants.MODEL, modelFilter);
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.MODEL, emptyFilter);
			allContentsModel.keySet().forEach(k -> emptyFilter.put(k, "0"));
		}

		HashMap<String, String> techFilter = new HashMap<>();
		List<Map<String,Object>> dbListTech =   learningContentRepo
				.findSuccessAcademyFiltered(Constants.TECHNOLOGY, learningItemIdsList);
		Map<String,String> allContentsTech = listToMap(dbListTech);
		techFilter.putAll(allContentsTech);
		if(!techFilter.isEmpty()) {
			countFilters.put(Constants.TECHNOLOGY, techFilter);
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.TECHNOLOGY, emptyFilter);
			allContentsTech.keySet().forEach(k -> emptyFilter.put(k, "0"));
		}

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
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.SUCCESS_TRACK, emptyFilter);
			allContentsST.keySet().forEach(k -> emptyFilter.put(k, "0"));
		}

		HashMap<String, String> docFilter = new HashMap<>();
		List<Map<String,Object>> dbListDoc =   learningContentRepo
				.getDocFilterCountByCards(learningItemIdsList);
		Map<String,String> allContentsDoc = listToMap(dbListDoc);
		docFilter.putAll(allContentsDoc);
		if(!docFilter.isEmpty()) {
			countFilters.put(Constants.DOCUMENTATION_FILTER, docFilter);
			HashMap<String, String> emptyFilter=new HashMap<>();
			filters.put(Constants.DOCUMENTATION_FILTER, emptyFilter);
			allContentsDoc.keySet().forEach(k -> emptyFilter.put(k, "0"));
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

	@Override
	public List<NewLearningContentEntity> fetchRecentlyViewedContent(String userId, Map<String, String> filterParams) {
		List<NewLearningContentEntity> result;
		List<NewLearningContentEntity> learningContentListSACampus = new ArrayList<>();
		Set<String> productDocSuccesstrackfilters=getSuccessTrackFilters(filterParams);
		if(filterParams.isEmpty())
			result= learningContentRepo.getRecentlyViewedContent(userId);
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			specification = specification.and(new SpecificationBuilder().filter(filterParams));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=learningContentRepo.getRecentlyViewedContentFiltered(userId, learningItemIdsList);
		}
		//filter content w.r.t product documentation successtrack filters
		if(!productDocSuccesstrackfilters.isEmpty()) {
			Set<String> learningContentIds=result.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			if(productDocSuccesstrackfilters.contains(Constants.CAMPUS_NETWORK)) {
				learningContentListSACampus=result.stream()
						.filter(entity->entity.getAssetFacet()!=null && entity.getAssetFacet().equals(Constants.CAMPUS)).collect(Collectors.toList());
			}
			result=learningContentRepo.getCardsBySt(productDocSuccesstrackfilters, learningContentIds);
			result.addAll(learningContentListSACampus);
		}
		return result;
	}

	@Override
	public List<NewLearningContentEntity> fetchFilteredContent(String puid, String ccoid,
			Map<String, String> query_map) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		List<NewLearningContentEntity> learningContentListCampusSA = new ArrayList<>();
		Set<String> productDocSuccesstrackfilters=getSuccessTrackFilters(query_map);
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilder().filter(query_map));
		filteredList = learningContentRepo.findAll(specification);
		//filter content w.r.t product documentation successtrack filters
		if(!productDocSuccesstrackfilters.isEmpty()) {
			Set<String> learningContentIds=filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			if(productDocSuccesstrackfilters.contains(Constants.CAMPUS_NETWORK)) {
				learningContentListCampusSA=filteredList.stream()
						.filter(entity->entity.getAssetFacet()!=null && entity.getAssetFacet().equals(Constants.CAMPUS)).collect(Collectors.toList());
			}
			filteredList=learningContentRepo.getCardsBySt(productDocSuccesstrackfilters, learningContentIds);
			filteredList.addAll(learningContentListCampusSA);
		}
		return filteredList;
	}
	
	@Override
	public List<NewLearningContentEntity> fetchUpcomingContent(Map<String, String> filterParams) {
		List<NewLearningContentEntity> result;
		if(filterParams.isEmpty())
			result= learningContentRepo.findUpcoming();
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			specification = specification.and(new SpecificationBuilder().filter(filterParams));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=learningContentRepo.findUpcomingFiltered(learningItemIdsList);
		}
		return result;
	}
	
	@Override
	public List<NewLearningContentEntity> fetchSuccessAcademyContent(Map<String, String> filterParams) {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification= specification.and(CustomSpecifications.hasValue(Constants.LEARNING_TYPE, Constants.SUCCESS_ACADEMY));
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		return learningContentRepo.findAll(specification);
	}

	private Set<String> getSuccessTrackFilters(Map<String, String> filterparams){
		Set<String> successTracks=new HashSet<>();
		String fieldValue = filterparams.get(Constants.SUCCESS_TRACK);
		if(fieldValue!=null){
			successTracks=new HashSet<>(Arrays.asList(fieldValue.split(",")));
			filterparams.remove(Constants.SUCCESS_TRACK);
		}
		return successTracks;
	}
}
