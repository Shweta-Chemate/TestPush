package com.cisco.cx.training.app.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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
import com.cisco.cx.training.app.dao.LearningBookmarkDAO;
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

	@Autowired
	private LearningBookmarkDAO learningBookmarkDAO;

	private static HashMap<String, List<String>> getAPIFilterGroupMappings() {
		HashMap<String, List<String>> APIFilterGroupMappings=new HashMap<>();
		APIFilterGroupMappings.put(Constants.NEW, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.UPCOMING_EVENTS, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE));
		APIFilterGroupMappings.put(Constants.BOOKMARKED, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.RECENTLY_VIEWED, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.CX_INSIGHTS, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.DOCUMENTATION_FILTER,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY,Constants.FOR_YOU_FILTER));
		return APIFilterGroupMappings;
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
	public HashMap<String, Object> getViewMoreNewFiltersWithCount(HashMap<String, Object> filtersSelected) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.NEW);
		filteredList = fetchNewLearningContent(new HashMap<String,List<String>>(), null);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		
		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		if(filtersSelected==null)
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filtersSelected!=null && !filtersSelected.isEmpty() && filtersSelected.size()==1)
			{
				filtersSelected.keySet().forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));

			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, null);
			return filters;
		}
	}

	@Override
	public HashMap<String, Object> getRecentlyViewedFiltersWithCount(String userId, HashMap<String, Object> filtersSelected) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.RECENTLY_VIEWED);
		filteredList = fetchRecentlyViewedContent(userId, new HashMap<String,List<String>>(), null);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		initializeFilters(countFilters);

		if(filtersSelected==null)
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filtersSelected!=null && !filtersSelected.isEmpty() && filtersSelected.size()==1)
			{
				filtersSelected.keySet().forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));

			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, null);
			return filters;
		}
	}

	@Override
	public HashMap<String, Object> getUpcomingFiltersWithCount(HashMap<String, Object> filtersSelected) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.UPCOMING_EVENTS);
		filteredList = fetchUpcomingContent( new HashMap<String,List<String>>(), null);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		if(filtersSelected==null)
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filtersSelected!=null && !filtersSelected.isEmpty() && filtersSelected.size()==1)
			{
				filtersSelected.keySet().forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));

			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, null);
			return filters;
		}
	}

	@Override
	public HashMap<String, Object> getBookmarkedFiltersWithCount(HashMap<String, Object> filtersSelected, List<LearningContentItem> bookmarkedList) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.BOOKMARKED);
		learningItemIdsList = bookmarkedList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		if(filtersSelected==null)
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filtersSelected!=null && !filtersSelected.isEmpty() && filtersSelected.size()==1)
			{
				filtersSelected.keySet().forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));

			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, null);
			return filters;
		}
	}

	@Override
	public HashMap<String, Object> getCXInsightsFiltersWithCount(String userId, String searchToken, HashMap<String, Object> filtersSelected) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.CX_INSIGHTS);
		filteredList = fetchCXInsightsContent(userId, new HashMap<String,List<String>>(), null, null, Constants.SORTDATE, Constants.DESC);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, userId);

		if(searchToken!=null) {
			filteredList = fetchCXInsightsContent(userId, new HashMap<String,List<String>>(), null, searchToken, Constants.SORTDATE, Constants.DESC);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			initializeFilters(countFilters);
			filterCountsDAO.setFilterCounts(learningItemIdsList, countFilters, "none", userId);
		}

		if(filtersSelected==null)
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, userId);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty())
				return filters;
			if(filtersSelected!=null && !filtersSelected.isEmpty() && filtersSelected.size()==1)
			{
				filtersSelected.keySet().forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));
			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, userId);
			return filters;
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeFilters(HashMap<String, Object> countFilters) {
		countFilters.keySet().forEach(filterGroup->{
			Object filter=countFilters.get(filterGroup);
			((Map) filter).keySet().forEach(key->{
				Object value=((Map)filter).get(key);
				if(value instanceof Map) {
					((Map) value).keySet().forEach(keyUc->{
						Object valueUc=((Map)value).get(keyUc);
						((Map) valueUc).keySet().forEach(KeyPt->{
							((Map) valueUc).put(KeyPt, "0");
						});
					});
				}
				else
					((Map)filter).put(key, "0");
			});

		});
	}

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent(Map<String, List<String>> queryMap, Object stMap) {
		List<NewLearningContentEntity> result;
		if(queryMap.isEmpty() && stMap==null)
			result= learningContentRepo.findNew();
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			SpecificationBuilder builder=new SpecificationBuilder();
			specification=getSpecificationForCuratedTags(queryMap ,stMap, null);
			specification = specification.and(builder.filter(queryMap));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=learningContentRepo.findNewFiltered(learningItemIdsList);
		}
		return result;
	}

	@Override
	public List<NewLearningContentEntity> fetchRecentlyViewedContent(String userId,  Map<String, List<String>> queryMap, Object stMap) {
		List<NewLearningContentEntity> result;
		if(queryMap.isEmpty() && stMap==null)
			result= learningContentRepo.getRecentlyViewedContent(userId);
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			SpecificationBuilder builder=new SpecificationBuilder();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			specification=getSpecificationForCuratedTags(queryMap ,stMap, null);
			specification = specification.and(builder.filter(queryMap));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=learningContentRepo.getRecentlyViewedContentFiltered(userId, learningItemIdsList);
		}
		return result;
	}

	@Override
	public List<NewLearningContentEntity> fetchCXInsightsContent(String userId, Map<String, List<String>> queryMap, Object stMap, String searchToken,
			String sortField, String sortType) {
		List<NewLearningContentEntity> result;
		Set<String> learningItemIdsList = new HashSet<String>();
		List<String> learningItemIdsListCXInsights = new ArrayList<String>();
		//get ids tagged with pitstop
		learningItemIdsListCXInsights=learningContentRepo.getPitstopTaggedContent();
		//get ids for foryou filter
		SpecificationBuilder builder=new SpecificationBuilder();
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = getSpecificationForCuratedTags(queryMap ,stMap, userId);
		specification = specification.and(builder.filter(queryMap));
		specification = specification.and(builder.buildSearchSpecification(searchToken));
		specification = specification.and(builder.filterById(learningItemIdsListCXInsights));
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
	public List<NewLearningContentEntity> fetchFilteredContent(Map<String, List<String>> queryMap, Object stMap) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		SpecificationBuilder builder=new SpecificationBuilder();
		specification=getSpecificationForCuratedTags(queryMap ,stMap, null);
		specification = specification.and(builder.filter(queryMap));
		filteredList = learningContentRepo.findAll(specification);
		return filteredList;
	}
	
	@Override
	public List<NewLearningContentEntity> fetchUpcomingContent(Map<String, List<String>> queryMap, Object stMap) {
		List<NewLearningContentEntity> result;
		if(queryMap.isEmpty() && stMap==null)
			result= learningContentRepo.findUpcoming();
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			SpecificationBuilder builder=new SpecificationBuilder();
			specification=getSpecificationForCuratedTags(queryMap ,stMap, null);
			specification = specification.and(builder.filter(queryMap));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=learningContentRepo.findUpcomingFiltered(learningItemIdsList);
		}
		return result;
	}
	
	@Override
	public LearningMap getLearningMap(String id) {
		List<LearningModule> learningModuleList = new ArrayList<>();
		LearningMap learningMap = new LearningMap();
		NewLearningContentEntity learningMapEntity =  learningContentRepo.findById(id).isPresent()?learningContentRepo.findById(id).get():null;
		if(learningMapEntity!=null)
		{
			List<NewLearningContentEntity> learningModuleEntityList = learningContentRepo.findByLearningTypeAndLearningMap(Constants.LEARNINGMODULE, learningMapEntity.getTitle());
			learningModuleEntityList.forEach(learningModuleEntity -> {
				LearningModule learningModule = (new LearningModule()).getLearningModuleFromEntity(learningModuleEntity);
				learningModuleList.add(learningModule);
			});
			learningMap = (new LearningMap()).getLearningMapFromEntity(learningMapEntity);
			learningMap.setLearningModules(learningModuleList.stream()
		            .sorted(Comparator.comparingInt(LearningModule::getSequence))
		            .collect(Collectors.toList()));	
		}
		return learningMap;
	}

	private Specification<NewLearningContentEntity> getSpecificationForCuratedTags(Map<String, List<String>> queryMap, Object stMap, String userId) {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		for (Entry<String, List<String>> filterParam : queryMap.entrySet()) {
			String key = filterParam.getKey();
			List<String> values = filterParam.getValue();
			if(key.equals(Constants.ROLE)) {
				List<String> learningItemIdsListRolesFiltered = new ArrayList<String>(learningContentRepo.getCardIdsByRole(new HashSet<String>(values)));
				specification=specification.and(CustomSpecifications.hasValueIn(Constants.ID, learningItemIdsListRolesFiltered));
			}
			if(key.equals(Constants.TECHNOLOGY)) {
				List<String> learningItemIdsListTechFiltered = new ArrayList<String>(learningContentRepo.getCardIdsByTech(new HashSet<String>(values)));
				specification=specification.and(CustomSpecifications.hasValueIn(Constants.ID, learningItemIdsListTechFiltered));
			}
			if(key.equals(Constants.LIFECYCLE)) {
				List<String> learningItemIdsListLFCFiltered = new ArrayList<String>(learningContentRepo.getPitstopTaggedContentFilter(new HashSet<String>(values)));
				specification=specification.and(CustomSpecifications.hasValueIn(Constants.ID, learningItemIdsListLFCFiltered));
			}
			if(key.equals(Constants.FOR_YOU_FILTER)) {
				List<String> learningItemIdsListForYouFiltered=new ArrayList<>();
				if(values.contains(Constants.NEW))
					learningItemIdsListForYouFiltered.addAll(fetchNewLearningContent(new HashMap<String, List<String>>(), null).stream().map(learningItem -> learningItem.getId()).collect(Collectors.toSet()));
				if(values.contains(Constants.BOOKMARKED))
					learningItemIdsListForYouFiltered.addAll(getBookMarkedIds(userId));
				if(values.contains(Constants.RECENTLY_VIEWED))
					learningItemIdsListForYouFiltered.addAll(fetchRecentlyViewedContent(userId, new HashMap<String, List<String>>(), null).stream().map(learningItem -> learningItem.getId()).collect(Collectors.toSet()));
				specification=specification.and(CustomSpecifications.hasValueIn(Constants.ID, learningItemIdsListForYouFiltered));
			}
		}
		if(stMap!=null) {
			List<String> learningItemIdsListSTFiltered = new ArrayList<String>(getSTFilteredIDs(stMap));
			specification=specification.and(new SpecificationBuilder().filterById(learningItemIdsListSTFiltered));
		}
		//remove all curated tags
		queryMap.remove(Constants.ROLE);
		queryMap.remove(Constants.TECHNOLOGY);
		queryMap.remove(Constants.LIFECYCLE);
		queryMap.remove(Constants.FOR_YOU_FILTER);
		return specification;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getSTFilteredIDs(Object stMap) {
		Set<String> cardIdsStUcPs = new HashSet<String>();
		//LOG.info("ST="+((Map) v).keySet());
		((Map) stMap).keySet().forEach(ik->{
			Object iv = ((Map)stMap).get(ik);
			List<String> ilist;
			if(iv instanceof Map) {
				//LOG.info("UC="+((Map) iv).keySet());
				((Map)iv).keySet().forEach(ivk -> {
					Object ivv = ((Map)iv).get(ivk);
					List<String> ivlist;
					if(ivv instanceof List)
					{
						ivlist= (List<String>)ivv;
						LOG.info("PS={} uc={} st={}",ivlist,ivk,ik);
						Set<String> pitStops = new HashSet<String>(ivlist);
						String usecase = ivk.toString();
						String successtrack = ik.toString();
						cardIdsStUcPs.addAll(learningContentRepo.getCardIdsByPsUcSt(successtrack,usecase,pitStops));
					}
				});
			}
		});
		return cardIdsStUcPs;
	}

	List<String> getBookMarkedIds(String userId){
		return new ArrayList<String>(learningBookmarkDAO.getBookmarks(userId));
	}

}
