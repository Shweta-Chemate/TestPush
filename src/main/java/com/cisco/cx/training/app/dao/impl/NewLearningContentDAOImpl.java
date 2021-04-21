package com.cisco.cx.training.app.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	public HashMap<String, HashMap<String,String>> getViewMoreNewFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCounts) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		if(filterCounts==null)
		{
			filterCounts=new HashMap<>();
		}
		else
		{
			filterCounts.values().forEach(filterGroup -> {
				filterGroup.keySet().forEach(key -> filterGroup.put(key, "0"));
			});
		}

		filteredList = fetchNewLearningContent(filter);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList);

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
		return result;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getRecentlyViewedFiltersWithCount(String puid, String userId, Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		if(filterCounts==null)
		{
			filterCounts=new HashMap<>();
		}
		else
		{
			filterCounts.values().forEach(filterGroup -> {
				filterGroup.keySet().forEach(key -> filterGroup.put(key, "0"));
			});
		}
		filteredList = fetchRecentlyViewedContent(puid, userId, filter);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList);
		return filterCounts;
	}

	@Override
	public List<NewLearningContentEntity> fetchFilteredContent(String puid, String ccoid,
			Map<String, String> query_map) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilder().filter(query_map));
		filteredList = learningContentRepo.findAll(specification);
		return filteredList;
	}
	
	@Override
	public HashMap<String, HashMap<String, String>> getBookmarkedFiltersWithCount(Map<String, String> query_map, HashMap<String, HashMap<String, String>> filterCounts,
			List<LearningContentItem> filteredBookmarkedList) {
		Set<String> learningItemIdsList = new HashSet<String>();

		if (filterCounts == null) {
			filterCounts = new HashMap<>();
		} else {
			filterCounts.values().forEach(filterGroup -> {
				filterGroup.keySet().forEach(key -> filterGroup.put(key, "0"));
			});
		}

		learningItemIdsList = filteredBookmarkedList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList);

		return filterCounts;
	}
	
	private Specification<NewLearningContentEntity> addUpcomingSpecifications() {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		LocalDateTime localDateTimeStart = LocalDateTime.now();
		ZonedDateTime zdtStart = ZonedDateTime.of(localDateTimeStart, ZoneId.systemDefault());
		specification= specification.and(CustomSpecifications.hasDateGreaterThan(Constants.SORTDATE,new Timestamp(zdtStart.toInstant().toEpochMilli())));
		specification= specification.and(CustomSpecifications.hasValue(Constants.CONTENT_TYPE_FIELD, Constants.LIVE_WEBINAR));
		return specification;
	}
	
	@Override
	public List<NewLearningContentEntity> fetchUpcomingContent(Map<String, String> filterParams) {
		Specification<NewLearningContentEntity> specification = addUpcomingSpecifications();
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		int offset=0;
		int limit = 25;
		return learningContentRepo.findAll(specification,PageRequest.of( offset, limit, Sort.by(Sort.Direction.fromString(Constants.ASC), Constants.SORTDATE))).getContent();
	}
	
	@Override
	public HashMap<String, HashMap<String, String>> getUpcomingFiltersWithCount(Map<String, String> filter,
			HashMap<String, HashMap<String, String>> filterCounts) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		if (filterCounts == null) {
			filterCounts = new HashMap<>();
		} else {
			filterCounts.values().forEach(filterGroup -> {
				filterGroup.keySet().forEach(key -> filterGroup.put(key, "0"));
			});
		}

		Specification<NewLearningContentEntity> specification = addUpcomingSpecifications();
		specification = specification.and(new SpecificationBuilder().filter(filter));
		int offset=0;
		int limit = 25;
		filteredList = learningContentRepo.findAll(specification,PageRequest.of( offset, limit, Sort.by(Sort.Direction.fromString(Constants.ASC), Constants.SORTDATE))).getContent();
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Calculating counts after filtering
		getFilteredCounts(filterCounts, learningItemIdsList);

		return filterCounts;
	}
	
	HashMap<String, HashMap<String, String>> getFilteredCounts(HashMap<String, HashMap<String, String>> filterCounts, Set<String> learningItemIdsList)
	{
		// Content Type Filter
		HashMap<String, String> contentTypeFilter = filterCounts.containsKey(Constants.CONTENT_TYPE)
				? filterCounts.get(Constants.CONTENT_TYPE)
				: new HashMap<>();
		List<Map<String, Object>> contentTypeFiltersWithCount = learningContentRepo
				.getAllContentTypeWithCountByCards(learningItemIdsList);
		Map<String, String> allContents = listToMap(contentTypeFiltersWithCount);
		contentTypeFilter.putAll(allContents);
		if (!contentTypeFilter.isEmpty())
			filterCounts.put("Content Type", contentTypeFilter);

		// Live Events Filter
		HashMap<String, String> regionFilter = filterCounts.containsKey(Constants.LIVE_EVENTS)
				? filterCounts.get(Constants.LIVE_EVENTS)
				: new HashMap<>();
		List<Map<String, Object>> regionFilterWithCount = learningContentRepo
				.getAllRegionsWithCountByCards(learningItemIdsList);
		Map<String, String> allRegions = listToMap(regionFilterWithCount);
		regionFilter.putAll(allRegions);
		if (!regionFilter.isEmpty())
			filterCounts.put("Live Events", regionFilter);

		// Language Filter
		HashMap<String, String> languageFilter = filterCounts.containsKey(Constants.LANGUAGE)
				? filterCounts.get(Constants.LANGUAGE)
				: new HashMap<>();
		List<Map<String, Object>> languageFiltered = learningContentRepo
				.getAllLanguagesWithCountByCards(learningItemIdsList);
		Map<String, String> allLanguages = listToMap(languageFiltered);
		languageFilter.putAll(allLanguages);
		if (!languageFilter.isEmpty())
			filterCounts.put("Language", languageFilter);
		
		return filterCounts;
	}

}
