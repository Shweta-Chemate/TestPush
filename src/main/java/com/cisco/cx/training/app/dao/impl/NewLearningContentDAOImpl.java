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

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent(Map<String, String> filterParams) {
		List<NewLearningContentEntity> result;
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
	public HashMap<String, HashMap<String,String>> getViewMoreNewFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCounts, String select) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		if(filterCounts==null)
		{
			filterCounts=new HashMap<>();
		}

		filteredList = fetchNewLearningContent(filter);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList, select);

		return filterCounts;
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
	public List<NewLearningContentEntity> fetchRecentlyViewedContent(String puid, String userId, Map<String, String> filterParams) {
		List<NewLearningContentEntity> result;
		List<NewLearningContentEntity> learningContentListSACampus = new ArrayList<>();
		Set<String> productDocSuccesstrackfilters=getSuccessTrackFilters(filterParams);
		if(filterParams.isEmpty())
			result= learningContentRepo.getRecentlyViewedContent(puid, userId);
		else {
			List<NewLearningContentEntity> filteredList = new ArrayList<>();
			Set<String> learningItemIdsList = new HashSet<String>();
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			specification = specification.and(new SpecificationBuilder().filter(filterParams));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());
			result=learningContentRepo.getRecentlyViewedContentFiltered(puid, userId, learningItemIdsList);
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
	public HashMap<String, HashMap<String, String>> getRecentlyViewedFiltersWithCount(String puid, String userId, Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCounts, String select) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		if(filterCounts==null)
		{
			filterCounts=new HashMap<>();
		}

		filteredList = fetchRecentlyViewedContent(puid, userId, filter);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList, select);
		
		// Calculating counts for successacademy filters if applicable
		getSuccessAcademyFilteredCounts(filterCounts, learningItemIdsList, select);

		// Calculating counts for product doc filters if applicable
		getProductDocumentationFilterCounts(filterCounts, learningItemIdsList, select);

		return filterCounts;
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
	public HashMap<String, HashMap<String, String>> getBookmarkedFiltersWithCount(Map<String, String> query_map, HashMap<String, HashMap<String, String>> filterCounts,
			List<LearningContentItem> filteredBookmarkedList, String select) {
		Set<String> learningItemIdsList = new HashSet<String>();

		if (filterCounts == null) {
			filterCounts = new HashMap<>();
		}

		learningItemIdsList = filteredBookmarkedList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList, select);
		
		// Calculating counts for successacademy filters if applicable
		getSuccessAcademyFilteredCounts(filterCounts, learningItemIdsList, select);

		// Calculating counts for product doc filters if applicable
		getProductDocumentationFilterCounts(filterCounts, learningItemIdsList, select);

		return filterCounts;
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
	public HashMap<String, HashMap<String, String>> getUpcomingFiltersWithCount(Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCounts, String select) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		if (filterCounts == null) {
			filterCounts = new HashMap<>();
		}

		filteredList = fetchUpcomingContent(filter);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList, select);

		return filterCounts;
	}
	
	@Override
	public List<NewLearningContentEntity> fetchSuccessAcademyContent(Map<String, String> filterParams) {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification= specification.and(CustomSpecifications.hasValue(Constants.LEARNING_TYPE, Constants.SUCCESS_ACADEMY));
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		return learningContentRepo.findAll(specification);
	}

	HashMap<String, HashMap<String, String>> getFilteredCounts(HashMap<String, HashMap<String, String>> filterCounts, Set<String> learningItemIdsList, String select)
	{
		boolean update=select==null?true:!select.equals(Constants.CONTENT_TYPE);
		// Content Type Filter
		if(update) {
			HashMap<String, String> contentTypeFilter = filterCounts.containsKey(Constants.CONTENT_TYPE)
					? filterCounts.get(Constants.CONTENT_TYPE)
							: new HashMap<>();
					contentTypeFilter.keySet().forEach(key -> contentTypeFilter.put(key, "0"));
					List<Map<String, Object>> contentTypeFiltersWithCount = learningContentRepo
							.getAllContentTypeWithCountByCards(learningItemIdsList);
					Map<String, String> allContents = listToMap(contentTypeFiltersWithCount);
					contentTypeFilter.putAll(allContents);
					if (!contentTypeFilter.isEmpty())
						filterCounts.put("Content Type", contentTypeFilter);
		}

		update=select==null?true:!select.equals(Constants.LIVE_EVENTS);
		// Live Events Filter
		if(update) {
			HashMap<String, String> regionFilter = filterCounts.containsKey(Constants.LIVE_EVENTS)
					? filterCounts.get(Constants.LIVE_EVENTS)
					: new HashMap<>();
			regionFilter.keySet().forEach(key -> regionFilter.put(key, "0"));
			List<Map<String, Object>> regionFilterWithCount = learningContentRepo
					.getAllRegionsWithCountByCards(learningItemIdsList);
			Map<String, String> allRegions = listToMap(regionFilterWithCount);
			regionFilter.putAll(allRegions);
			if (!regionFilter.isEmpty())
				filterCounts.put("Live Events", regionFilter);
		}

		update=select==null?true:!select.equals(Constants.LANGUAGE);
		// Language Filter
		if(update) {
			HashMap<String, String> languageFilter = filterCounts.containsKey(Constants.LANGUAGE)
					? filterCounts.get(Constants.LANGUAGE)
					: new HashMap<>();
			languageFilter.keySet().forEach(key -> languageFilter.put(key, "0"));
			List<Map<String, Object>> languageFiltered = learningContentRepo
					.getAllLanguagesWithCountByCards(learningItemIdsList);
			Map<String, String> allLanguages = listToMap(languageFiltered);
			languageFilter.putAll(allLanguages);
			if (!languageFilter.isEmpty())
				filterCounts.put("Language", languageFilter);
		}

		return filterCounts;
	}
	
	@Override
	public HashMap<String, HashMap<String, String>> getSuccessAcademyFiltersWithCount(Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCounts, String select) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		if(filterCounts==null)
		{
			filterCounts=new HashMap<>();
		}

		filteredList = fetchSuccessAcademyContent(filter);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getSuccessAcademyFilteredCounts(filterCounts, learningItemIdsList, select);
		return filterCounts;
	}

	HashMap<String, HashMap<String, String>> getSuccessAcademyFilteredCounts(HashMap<String, HashMap<String, String>> filterCounts, Set<String> learningItemIdsList, String select)
	{
		boolean update=select==null?true:!select.equals(Constants.ROLE);
		// Role Filter
		if(update) {
			HashMap<String, String> roleFilter = filterCounts.containsKey(Constants.ROLE)
					? filterCounts.get(Constants.ROLE)
							: new HashMap<>();
					roleFilter.keySet().forEach(key -> roleFilter.put(key, "0"));
					List<Map<String, Object>> roleFiltersWithCount = learningContentRepo
							.findSuccessAcademyFiltered(Constants.ROLE, learningItemIdsList);
					Map<String, String> allRoles = listToMap(roleFiltersWithCount);
					roleFilter.putAll(allRoles);
					if (!roleFiltersWithCount.isEmpty())
						filterCounts.put(Constants.ROLE, roleFilter);
		}
		
		update=select==null?true:!select.equals(Constants.MODEL);
		// Model Filter
		if(update) {
			HashMap<String, String> modelFilter = filterCounts.containsKey(Constants.MODEL)
					? filterCounts.get(Constants.MODEL)
					: new HashMap<>();
					modelFilter.keySet().forEach(key -> modelFilter.put(key, "0"));
			List<Map<String, Object>> modelFilterWithCount = learningContentRepo
					.findSuccessAcademyFiltered(Constants.MODEL, learningItemIdsList);
			Map<String, String> allModels = listToMap(modelFilterWithCount);
			modelFilter.putAll(allModels);
			if (!modelFilter.isEmpty())
				filterCounts.put(Constants.MODEL, modelFilter);
		}
		
		update=select==null?true:!select.equals(Constants.SUCCESS_TRACK);
		// Success Track Filter
		if(update) {
			HashMap<String, String> successTrackFilter = filterCounts.containsKey(Constants.SUCCESS_TRACK)
					? filterCounts.get(Constants.SUCCESS_TRACK)
					: new HashMap<>();
					successTrackFilter.keySet().forEach(key -> successTrackFilter.put(key, "0"));
			List<Map<String, Object>> successTrackFiltWithCount = learningContentRepo
					.findSuccessAcademyFiltered(Constants.SUCCESS_TRACK, learningItemIdsList);
			Map<String, String> allSuccessTracks = listToMap(successTrackFiltWithCount);
			if(allSuccessTracks.containsKey(Constants.CAMPUS)) {
				allSuccessTracks.put(Constants.CAMPUS_NETWORK,allSuccessTracks.get(Constants.CAMPUS));
				allSuccessTracks.remove(Constants.CAMPUS);
			}
			successTrackFilter.putAll(allSuccessTracks);
			if (!successTrackFilter.isEmpty())
				filterCounts.put(Constants.SUCCESS_TRACK, successTrackFilter);
		}
		
		update=select==null?true:!select.equals(Constants.TECHNOLOGY);
		// Technology Filter
		if(update) {
			HashMap<String, String> technologyFilter = filterCounts.containsKey(Constants.TECHNOLOGY)
					? filterCounts.get(Constants.TECHNOLOGY)
					: new HashMap<>();
					technologyFilter.keySet().forEach(key -> technologyFilter.put(key, "0"));
			List<Map<String, Object>> technologyFilterWithCount = learningContentRepo
					.findSuccessAcademyFiltered(Constants.TECHNOLOGY, learningItemIdsList);
			Map<String, String> allTechnologies = listToMap(technologyFilterWithCount);
			technologyFilter.putAll(allTechnologies);
			if (!technologyFilter.isEmpty())
				filterCounts.put(Constants.TECHNOLOGY, technologyFilter);
		}
		
		return filterCounts;
	}

	HashMap<String, HashMap<String, String>> getProductDocumentationFilterCounts(HashMap<String, HashMap<String, String>> filterCounts, Set<String> learningItemIdsList, String select){
		boolean update=select==null?true:!select.equals(Constants.SUCCESS_TRACK);
		// Success Track Filter
		if(update) {
			HashMap<String, String> successTrackFilter = filterCounts.containsKey(Constants.SUCCESS_TRACK)
					? filterCounts.get(Constants.SUCCESS_TRACK)
							: new HashMap<>();
					List<Map<String, Object>> successTrackFilterWithCountPD = learningContentRepo
							.getAllStWithCountByCards(learningItemIdsList);
					Map<String, String> allSuccessTracksPD = listToMap(successTrackFilterWithCountPD);
					Map<String, String> allSuccessTracks=new HashMap<>(successTrackFilter);
					allSuccessTracksPD.forEach((k, v) -> allSuccessTracks.merge(k, v, (count1,count2)->
							Integer.toString(Integer.valueOf(count1)+Integer.valueOf(count2))));
					successTrackFilter.putAll(allSuccessTracks);
					if (!successTrackFilter.isEmpty())
						filterCounts.put(Constants.SUCCESS_TRACK, successTrackFilter);
		}
		return filterCounts;
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
