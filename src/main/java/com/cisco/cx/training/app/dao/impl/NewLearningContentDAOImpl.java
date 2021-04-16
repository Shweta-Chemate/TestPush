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
	public List<NewLearningContentEntity> fetchNewLearningContent(Map<String,List<String>> filterParams) {
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
	public HashMap<String, Object> getViewMoreFiltersWithCount(Map<String, String> filter) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();

		// Initial Load of filters
		// Content Type Filter
		HashMap<String, Object> filters = new HashMap<>();
		HashMap<String, String> contentTypeFilter = new HashMap<>();
		filters.put("Content Type", contentTypeFilter);
		List<Map<String, Object>> contentTypeFiltersWithCount = learningContentRepo.getAllContentTypeWithCount();
		Map<String, String> allContents = listToMap(contentTypeFiltersWithCount);
		contentTypeFilter.putAll(allContents);
		// Live Events Filter
		HashMap<String, String> regionFilter = new HashMap<>();
		filters.put("Live Events", regionFilter);
		List<Map<String, Object>> regionFilterWithCount = learningContentRepo.getAllRegionsTypeWithCount();
		Map<String, String> allRegions = listToMap(regionFilterWithCount);
		regionFilter.putAll(allRegions);
		// Language Filter
		HashMap<String, String> languageFilter = new HashMap<>();
		filters.put("Language", languageFilter);
		List<Map<String, Object>> languageFilterWithCount = learningContentRepo.getAllLanguagesTypeWithCount();
		Map<String, String> allLanguages = listToMap(languageFilterWithCount);
		languageFilter.putAll(allLanguages);

		// If filters are applied
		if (filter != null && !filter.isEmpty()) {
			Specification<NewLearningContentEntity> specification = Specification.where(null);
			specification = specification.and(new SpecificationBuilder().buildFilterSpecificationForViewMoreFilters(filter));
			filteredList = learningContentRepo.findAll(specification);
			learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
					.collect(Collectors.toSet());

			allContents.keySet().forEach(key -> contentTypeFilter.put(key, "0"));
			List<Map<String, Object>> contentTypeFiltered = learningContentRepo
					.getAllContentTypeWithCountByCards(learningItemIdsList);
			allContents = listToMap(contentTypeFiltered);
			contentTypeFilter.putAll(allContents);

			allRegions.keySet().forEach(key -> regionFilter.put(key, "0"));
			List<Map<String, Object>> regionFiltered = learningContentRepo
					.getAllRegionsWithCountByCards(learningItemIdsList);
			allRegions = listToMap(regionFiltered);
			regionFilter.putAll(allRegions);

			allLanguages.keySet().forEach(key -> languageFilter.put(key, "0"));
			List<Map<String, Object>> languageFiltered = learningContentRepo
					.getAllLanguagesWithCountByCards(learningItemIdsList);
			allLanguages = listToMap(languageFiltered);
			languageFilter.putAll(allLanguages);
		}

		return filters;
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
	public List<NewLearningContentEntity> fetchRecentlyViewedContent(String puid, String userId, Map<String, List<String>> filterParams) {
		List<NewLearningContentEntity> filteredList = new ArrayList<>();
		Set<String> learningItemIdsList = new HashSet<String>();
		Specification<NewLearningContentEntity> specification = Specification.where(null);
		specification = specification.and(new SpecificationBuilder().filter(filterParams));
		filteredList = learningContentRepo.findAll(specification);
		learningItemIdsList = filteredList.stream().map(learningItem -> learningItem.getId())
				.collect(Collectors.toSet());
		return learningContentRepo.getRecentlyViewedContent(puid, userId, learningItemIdsList);
	}

}
