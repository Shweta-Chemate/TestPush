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
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private final static HashMap<String, List<String>> APIFilterGroupMappings=getAPIFilterGroupMappings();

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Autowired
	private FilterCountsDAO filterCountsDAO;

	@Autowired
	private LearningBookmarkDAO learningBookmarkDAO;

	@Value("${popular.across.partner.companies.limitpercategory}")
	public Integer popularAcrossPartnersCategoryLiimit;

	@Value("${popular.at.partner.company.display.limit}")
	public Integer popularAtPartnerCompanyLimit;

	private static HashMap<String, List<String>> getAPIFilterGroupMappings() {
		HashMap<String, List<String>> APIFilterGroupMappings=new HashMap<>();
		APIFilterGroupMappings.put(Constants.NEW, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.UPCOMING_EVENTS, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE));
		APIFilterGroupMappings.put(Constants.BOOKMARKED, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.RECENTLY_VIEWED, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.CX_INSIGHTS, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY,Constants.FOR_YOU_FILTER));
		APIFilterGroupMappings.put(Constants.POPULAR_ACROSS_PARTNERS, Arrays.asList(Constants.LANGUAGE,Constants.LIVE_EVENTS,Constants.CONTENT_TYPE
				,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
		APIFilterGroupMappings.put(Constants.FEATURED_CONTENT, Arrays.asList(Constants.LANGUAGE,Constants.CONTENT_TYPE
				,Constants.SUCCESS_TRACK,Constants.ROLE,Constants.LIFECYCLE,Constants.TECHNOLOGY));
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

		//get all filter groups to be considered for the section
		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.NEW);
		//fetch learning content for the section
		filteredList = fetchNewLearningContent(new HashMap<String,List<String>>(), null);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		
		//initialize filter counts
		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		if(filtersSelected==null || filtersSelected.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty()) {
				return filters;
			}
			if(filtersSelected.size()==1)
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

		if(filtersSelected==null || filtersSelected.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty()) {
				return filters;
			}
			if(filtersSelected.size()==1)
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

		if(filtersSelected==null || filtersSelected.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty()) {
				return filters;
			}
			if(filtersSelected.size()==1)
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

		if(filtersSelected==null || filtersSelected.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty()) {
				return filters;
			}
			if(filtersSelected.size()==1)
			{
				filtersSelected.keySet().forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));

			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, null);
			return filters;
		}
	}

	@Override
	public HashMap<String, Object> getPopularAcrossPartnersFiltersWithCount(HashMap<String, Object> filtersSelected) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.POPULAR_ACROSS_PARTNERS);
		filteredList = fetchPopularAcrossPartnersContent(new HashMap<String,List<String>>(), null);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		if(filtersSelected==null || filtersSelected.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty()) {
				return filters;
			}
			if(filtersSelected.size()==1)
			{
				filtersSelected.keySet().forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));

			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, null);
			return filters;
		}
	}

	@Override
	public HashMap<String, Object> getPopularAtPartnerFiltersWithCount(HashMap<String, Object> filtersSelected,
			String puid) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds =  new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups=NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.POPULAR_ACROSS_PARTNERS);
		filteredList = fetchPopularAtPartnerContent(new HashMap<String,List<String>>(), null, puid);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		if(filtersSelected==null || filtersSelected.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty()) {
				return filters;
			}
			if(filtersSelected.size()==1)
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

		if(filtersSelected==null || filtersSelected.isEmpty())
		{
			return countFilters;
		}else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, userId);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if(cardIds.isEmpty()) {
				return filters;
			}
			if(filtersSelected.size()==1)
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
				else {
					((Map)filter).put(key, "0");
				}
			});

		});
	}

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent(Map<String, List<String>> queryMap, Object stMap) {
		List<NewLearningContentEntity> result;
		if(queryMap.isEmpty() && stMap==null) {
			result= learningContentRepo.findNew();
			}
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
		if(queryMap.isEmpty() && stMap==null) {
			result= learningContentRepo.getRecentlyViewedContent(userId);
		}
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
	public List<NewLearningContentEntity> fetchPopularAcrossPartnersContent(Map<String, List<String>> queryMap,
			Object stMap) {
		List<NewLearningContentEntity> result;
		if(queryMap.isEmpty() && stMap==null) {
			result= learningContentRepo.getPopularAcrossPartners(popularAcrossPartnersCategoryLiimit);
		}
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
			result=learningContentRepo.getPopularAcrossPartnersFiltered(learningItemIdsList, popularAcrossPartnersCategoryLiimit);
		}
		return result;
	}

	@Override
	public List<NewLearningContentEntity> fetchPopularAtPartnerContent(Map<String, List<String>> queryMap,
			Object stMap, String puid) {
		List<NewLearningContentEntity> result;
		if(queryMap.isEmpty() && stMap==null) {
			result= learningContentRepo.getPopularAtPartner(puid, popularAtPartnerCompanyLimit);
		}
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
			result=learningContentRepo.getPopularAtPartnerFiltered(puid, learningItemIdsList, popularAtPartnerCompanyLimit);
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
		SpecificationBuilder builder=new SpecificationBuilder();
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = getSpecificationForCuratedTags(queryMap ,stMap, userId);
		specification = specification.and(builder.filter(queryMap));
		specification = specification.and(builder.buildSearchSpecification(searchToken));
		specification = specification.and(builder.filterById(learningItemIdsListCXInsights));
		specification = specification.and(CustomSpecifications.notEqual(Constants.LEARNING_TYPE, Constants.DOCUMENTATION));
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
		if(queryMap.isEmpty() && stMap==null) {
			result= learningContentRepo.findUpcoming();
		}
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
	public LearningMap getLearningMap(String id, String title) {
		List<LearningModule> learningModuleList = new ArrayList<>();
		LearningMap learningMap = new LearningMap();
		NewLearningContentEntity learningMapEntity = new NewLearningContentEntity();
		if(id!=null)
		{
			Optional<NewLearningContentEntity> optFirst = learningContentRepo.findById(id);
			learningMapEntity =  optFirst.isPresent()?optFirst.get():null;
		}
		else if(title!=null)
		{
			learningMapEntity =  learningContentRepo.findByTitle(title);
		}
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

	/*
	* get specification for filters which cannot be filtered directly on the learning content specification
	* For example : Specification for all filters that can have multiple
	* values populated in the table like role, technology etc is built here.
	*/
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
				if(values.contains(Constants.BOOKMARKED_FOR_YOU)) {
					learningItemIdsListForYouFiltered.addAll(getBookMarkedIds(userId));
					}
				if(values.contains(Constants.RECENTLY_VIEWED)) {
					learningItemIdsListForYouFiltered.addAll(fetchRecentlyViewedContent(userId, new HashMap<String, List<String>>(), null).stream().map(learningItem -> learningItem.getId()).collect(Collectors.toSet()));
					}
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
		Set<String> cardIdsStUc = new HashSet<String>();
		//LOG.info("ST="+((Map) v).keySet()); //NOSONAR
		((Map) stMap).keySet().forEach(ik->{
			Object iv = ((Map)stMap).get(ik);
			List<String> ilist;
			if(iv instanceof Map) {
				//LOG.info("UC="+((Map) iv).keySet()); //NOSONAR
				Set<String> usecases= ((Map) iv).keySet(); String successtrack = ik.toString();
				cardIdsStUc.addAll(learningContentRepo.getCardIdsByUcSt(successtrack, usecases));
			}
		});
		return cardIdsStUc;
	}

	List<String> getBookMarkedIds(String userId){
		return new ArrayList<String>(learningBookmarkDAO.getBookmarks(userId));
	}
	
	@Override
	public Integer getSuccessTracksCount() {
		return learningContentRepo.getSuccessTracksCount();
	}
	
	@Override
	public Integer getLifecycleCount() {
		return learningContentRepo.getLifecycleCount();
	}
	
	@Override
	public Integer getTechnologyCount() {
		return learningContentRepo.getTechnologyount();
	}
	
	@Override
	public Integer getRolesCount() {
		return learningContentRepo.getRolesCount();
	}
	
	@Override
	public List<NewLearningContentEntity> fetchFeaturedContent(Map<String, List<String>> queryMap, Object stMap) {
		List<NewLearningContentEntity> result;
		if (queryMap.isEmpty() && stMap == null) {
			result = learningContentRepo.findFeatured();
		}
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			SpecificationBuilder builder = new SpecificationBuilder();
			specification = getSpecificationForCuratedTags(queryMap, stMap, null);
			specification = specification.and(builder.filter(queryMap));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result = learningContentRepo.findFeaturedFiltered(learningItemIdsList);
		}
		return result;
	}

	@Override
	public HashMap<String, Object> getFeaturedFiltersWithCount(HashMap<String, Object> filtersSelected) {
		Map<String, Set<String>> filteredCardsMap = new HashMap<String, Set<String>>();
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Set<String> cardIds = new HashSet<String>();
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, Object> countFilters = new HashMap<>();

		List<String> filterGroups = NewLearningContentDAOImpl.APIFilterGroupMappings.get(Constants.FEATURED_CONTENT);
		filteredList = fetchFeaturedContent(new HashMap<String, List<String>>(), null);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		filterCountsDAO.initializeFiltersWithCounts(filterGroups, filters, countFilters, learningItemIdsList, null);

		if (filtersSelected == null || filtersSelected.isEmpty()) {
			return countFilters;
		} else {
			filteredCardsMap = filterCountsDAO.filterCards(filtersSelected, learningItemIdsList, null);
			cardIds = filterCountsDAO.andFilters(filteredCardsMap);
			if (cardIds.isEmpty()) {
				return filters;
			}
			if (filtersSelected.size() == 1) {
				filtersSelected.keySet()
						.forEach(filterGroup -> filters.put(filterGroup, countFilters.get(filterGroup)));

			}
			filterCountsDAO.setFilterCounts(cardIds, filters, filteredCardsMap, null);
			return filters;
		}
	}

}
