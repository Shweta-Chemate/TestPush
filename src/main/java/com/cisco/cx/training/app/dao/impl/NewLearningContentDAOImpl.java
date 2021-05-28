package com.cisco.cx.training.app.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import com.cisco.cx.training.models.LearningMap;
import com.cisco.cx.training.models.LearningModule;

@Repository
public class NewLearningContentDAOImpl implements NewLearningContentDAO{

	private final static Logger LOG = LoggerFactory.getLogger(NewLearningContentDAOImpl.class);

	private final static HashMap<String, List<String>> APIFilterGroupMappings=getAPIFilterGroupMappings();

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Autowired
	private FilterCountsDAO filterCountsDAO;

	private static HashMap<String, List<String>> getAPIFilterGroupMappings() {
		HashMap<String, List<String>> APIFilterGroupMappings=new HashMap<>();
		APIFilterGroupMappings.put(Constants.NEW, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK));
		APIFilterGroupMappings.put(Constants.UPCOMING_EVENTS, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.MODEL,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.BOOKMARKED, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.MODEL,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.RECENTLY_VIEWED, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.MODEL,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.CX_COLLECTION, Arrays.asList(Constants.SUCCESS_TRACK,Constants.ROLE,Constants.MODEL,Constants.TECHNOLOGY));
		return APIFilterGroupMappings;
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
	
	void initializeFilters(HashMap<String, HashMap<String,String>> filters) {
		filters.keySet().forEach(filterGroup->{
			HashMap<String,String> filter=filters.get(filterGroup);
			HashMap<String,String> tempFilter=new HashMap<>();
			filter.keySet().forEach(key->{
				tempFilter.put(key, "0");
			});
			filters.put(filterGroup, tempFilter);
		});
	}

	@Override
	public HashMap<String, HashMap<String,String>> getViewMoreNewFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCountsMap) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.NEW);
		filteredList = fetchNewLearningContent(new HashMap<String,String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		if(filter.isEmpty())
		{
			filterCountsDAO.initializeFiltersWithCounts(filterGroups, countFilters, learningItemIdsList);
			return countFilters;
		}else {
			filters.putAll(filterCountsMap);
			initializeFilters(filters);
			countFilters.putAll(filterCountsMap);
			filteredCardsMap = filterCountsDAO.filterCards(filter, learningItemIdsList);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(FilterCountsDAOImpl.filterGroupMappings.get(k),
						countFilters.get(FilterCountsDAOImpl.filterGroupMappings.get(k))));
			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap);
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

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.RECENTLY_VIEWED);
		filteredList = fetchRecentlyViewedContent(userId, new HashMap<String,String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		if(filter.isEmpty())
		{
			filterCountsDAO.initializeFiltersWithCounts(filterGroups, countFilters, learningItemIdsList);
			return countFilters;
		}else {
			filters.putAll(filterCountsMap);
			initializeFilters(filters);
			countFilters.putAll(filterCountsMap);
			filteredCardsMap = filterCountsDAO.filterCards(filter, learningItemIdsList);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(FilterCountsDAOImpl.filterGroupMappings.get(k),
						countFilters.get(FilterCountsDAOImpl.filterGroupMappings.get(k))));
			}
			filterCountsDAO.setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
	}

	@Override
	public HashMap<String, HashMap<String, String>> getUpcomingFiltersWithCount(Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCountsMap) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.UPCOMING_EVENTS);
		filteredList = fetchUpcomingContent(new HashMap<String,String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		if(filter.isEmpty())
		{
			filterCountsDAO.initializeFiltersWithCounts(filterGroups, countFilters, learningItemIdsList);
			return countFilters;
		}else {
			filters.putAll(filterCountsMap);
			initializeFilters(filters);
			countFilters.putAll(filterCountsMap);
			filteredCardsMap = filterCountsDAO.filterCards(filter, learningItemIdsList);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(FilterCountsDAOImpl.filterGroupMappings.get(k),
						countFilters.get(FilterCountsDAOImpl.filterGroupMappings.get(k))));
			}
			filterCountsDAO.setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
	}

	@Override
	public HashMap<String, HashMap<String, String>> getBookmarkedFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String, String>> filterCountsMap,
			List<LearningContentItem> filteredBookmarkedList) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.BOOKMARKED);
		learningItemIdsList = filteredBookmarkedList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		if(filter.isEmpty())
		{
			filterCountsDAO.initializeFiltersWithCounts(filterGroups, countFilters, learningItemIdsList);
			return countFilters;
		}else {
			filters.putAll(filterCountsMap);
			initializeFilters(filters);
			countFilters.putAll(filterCountsMap);
			filteredCardsMap = filterCountsDAO.filterCards(filter, learningItemIdsList);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(FilterCountsDAOImpl.filterGroupMappings.get(k),
						countFilters.get(FilterCountsDAOImpl.filterGroupMappings.get(k))));
			}
			filterCountsDAO.setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
	}

	@Override
	public HashMap<String, HashMap<String, String>> getSuccessAcademyFiltersWithCount(Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCountsMap) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, HashMap<String,String>> filters = new HashMap<>();
		HashMap<String, HashMap<String,String>> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.CX_COLLECTION);
		filteredList = fetchSuccessAcademyContent(new HashMap<String, String>());
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		if(filter.isEmpty())
		{
			filterCountsDAO.initializeFiltersWithCounts(filterGroups, countFilters, learningItemIdsList);
			return countFilters;
		}else {
			filters.putAll(filterCountsMap);
			initializeFilters(filters);
			countFilters.putAll(filterCountsMap);
			filteredCardsMap = filterCountsDAO.filterCards(filter, learningItemIdsList);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filter.size()==1)
			{
				filter.keySet().forEach(k -> filters.put(FilterCountsDAOImpl.filterGroupMappings.get(k),
						countFilters.get(FilterCountsDAOImpl.filterGroupMappings.get(k))));
			}
			filterCountsDAO.setFilterCounts(cardIds,filters,filteredCardsMap);
			return filters;
		}
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
	public List<NewLearningContentEntity> fetchCXInsightsContent(Map<String, String> filterParams, String searchToken,
			String sortField, String sortType) {
		List<NewLearningContentEntity> result;
		List<NewLearningContentEntity> filteredListForYou = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		List<String> learningItemIdsListForYou = new ArrayList<String>();
		if(filterParams.containsKey(Constants.FOR_YOU_FILTER)) {
			List<String> filtersForYou=Arrays.asList(filterParams.get(Constants.FOR_YOU_FILTER).split(","));
			filterParams.remove(Constants.FOR_YOU_FILTER);
			filtersForYou.forEach(filter->{
				if(filter.equals(Constants.NEW))
					filteredListForYou.addAll(fetchNewLearningContent(filterParams));
			});
			learningItemIdsListForYou=filteredListForYou.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toList());
		}
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		specification=specification.and(new SpecificationBuilder().buildSearchSpecification(searchToken));
		specification=specification.and(new SpecificationBuilder().filterById(learningItemIdsListForYou));
		if(sortField.equals(Constants.TITLE))
		{
			result=learningContentRepo.findAll(specification);
			learningItemIdsList = result.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=sortType.equals(Constants.ASC)?learningContentRepo.getSortedByTitleAsc(learningItemIdsList):learningContentRepo.getSortedByTitleDesc(learningItemIdsList);
			
		}
		else
		{
			Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortField);
			result=learningContentRepo.findAll(specification,sort);	
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
	
	@Override
	public LearningMap getLearningMap(String id) {
		List<LearningModule> learningModuleList = new ArrayList<>();
		NewLearningContentEntity learningMapEntity = learningContentRepo.findById(id).get();
		List<NewLearningContentEntity> learningModuleEntityList = learningContentRepo.findByLearningTypeAndLearningMap(Constants.LEARNINGMODULE, learningMapEntity.getTitle());
		learningModuleEntityList.forEach(learningModuleEntity -> {
			LearningModule learningModule = (new LearningModule()).getLearningModuleFromEntity(learningModuleEntity);
			learningModuleList.add(learningModule);
		});
		LearningMap learningMap = (new LearningMap()).getLearningMapFromEntity(learningMapEntity);
		learningMap.setLearningModules(learningModuleList.stream()
	            .sorted(Comparator.comparingInt(LearningModule::getSequence))
	            .collect(Collectors.toList()));
		return learningMap;
	}

}
