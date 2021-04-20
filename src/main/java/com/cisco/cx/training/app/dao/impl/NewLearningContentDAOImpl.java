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
import com.cisco.cx.training.models.SuccessTalk;

@Repository
public class NewLearningContentDAOImpl implements NewLearningContentDAO{

	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent(Map<String, String> filterParams) {
		Specification<NewLearningContentEntity> specification = addTimeRangeSpecification();
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		return learningContentRepo.findAll(specification,Sort.by(Sort.Direction.fromString(Constants.DESC),Constants.SORTDATE));
	}

	private Specification<NewLearningContentEntity> addTimeRangeSpecification() {
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		LocalDateTime localDateTimeStart = LocalDateTime.now().minusMonths(3);
		ZonedDateTime zdtStart = ZonedDateTime.of(localDateTimeStart, ZoneId.systemDefault());
		LocalDateTime localDateTimeEnd = LocalDateTime.now();
		ZonedDateTime zdtEnd = ZonedDateTime.of(localDateTimeEnd, ZoneId.systemDefault());
		specification= specification.and(CustomSpecifications.hasDateBetweenCriteria(Constants.SORTDATE,new Timestamp(zdtStart.toInstant().toEpochMilli()),new Timestamp(zdtEnd.toInstant().toEpochMilli())));
		return specification;
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
	public HashMap<String, HashMap<String,String>> getViewMoreFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCounts) {
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

		Specification<NewLearningContentEntity> specification = addTimeRangeSpecification();
		specification = specification.and(new SpecificationBuilder().filter(filter));
		filteredList = learningContentRepo.findAll(specification);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Content Type Filter
		HashMap<String, String> contentTypeFilter = filterCounts.containsKey(Constants.CONTENT_TYPE) ? filterCounts.get(Constants.CONTENT_TYPE) : new HashMap<>();
		filterCounts.put("Content Type", contentTypeFilter);
		List<Map<String, Object>> contentTypeFiltersWithCount = learningContentRepo
				.getAllContentTypeWithCountByCards(learningItemIdsList);
		Map<String, String>  allContents = listToMap(contentTypeFiltersWithCount);
		contentTypeFilter.putAll(allContents);

		// Live Events Filter
		HashMap<String, String> regionFilter = filterCounts.containsKey(Constants.LIVE_EVENTS) ? filterCounts.get(Constants.LIVE_EVENTS) : new HashMap<>();
		filterCounts.put("Live Events", regionFilter);
		List<Map<String, Object>> regionFilterWithCount = learningContentRepo
				.getAllRegionsWithCountByCards(learningItemIdsList);
		Map<String, String> allRegions = listToMap(regionFilterWithCount);
		regionFilter.putAll(allRegions);

		// Language Filter
		HashMap<String, String> languageFilter = filterCounts.containsKey(Constants.LANGUAGE) ? filterCounts.get(Constants.LANGUAGE) : new HashMap<>();
		filterCounts.put("Language", languageFilter);
		List<Map<String, Object>> languageFiltered = learningContentRepo
				.getAllLanguagesWithCountByCards(learningItemIdsList);
		Map<String, String> allLanguages = listToMap(languageFiltered);
		languageFilter.putAll(allLanguages);

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
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		filteredList = learningContentRepo.findAll(specification);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		return learningContentRepo.getRecentlyViewedContent(puid, userId, learningItemIdsList);
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

		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilder().filter(filter));
		filteredList = learningContentRepo.findAll(specification);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());

		// Content Type Filter
		HashMap<String, String> contentTypeFilter = filterCounts.containsKey(Constants.CONTENT_TYPE) ? filterCounts.get(Constants.CONTENT_TYPE) : new HashMap<>();
		filterCounts.put("Content Type", contentTypeFilter);
		List<Map<String, Object>> contentTypeFiltersWithCount = learningContentRepo
				.getContentTypeFilteredForRecentlyViewed(puid, userId, learningItemIdsList);
		Map<String, String>  allContents = listToMap(contentTypeFiltersWithCount);
		contentTypeFilter.putAll(allContents);

		// Live Events Filter
		HashMap<String, String> regionFilter = filterCounts.containsKey(Constants.LIVE_EVENTS) ? filterCounts.get(Constants.LIVE_EVENTS) : new HashMap<>();
		filterCounts.put("Live Events", regionFilter);
		List<Map<String, Object>> regionFilterWithCount = learningContentRepo
				.getRegionFilteredForRecentlyViewed(puid, userId, learningItemIdsList);
		Map<String, String> allRegions = listToMap(regionFilterWithCount);
		regionFilter.putAll(allRegions);

		// Language Filter
		HashMap<String, String> languageFilter = filterCounts.containsKey(Constants.LANGUAGE) ? filterCounts.get(Constants.LANGUAGE) : new HashMap<>();
		filterCounts.put("Language", languageFilter);
		List<Map<String, Object>> languageFiltered = learningContentRepo
				.getLanguageFilteredForRecentlyViewed(puid, userId, learningItemIdsList);
		Map<String, String> allLanguages = listToMap(languageFiltered);
		languageFilter.putAll(allLanguages);

		return filterCounts;
	}

	@Override
	public List<NewLearningContentEntity> fetchFilteredContent(String puid, String ccoid,
			Map<String, String> query_map) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilder().filter(query_map));
		filteredList = learningContentRepo.findAll(specification);
		return null;
	}

}
