package com.cisco.cx.training.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningMap;

public interface NewLearningContentDAO {
  
	List<NewLearningContentEntity> fetchNewLearningContent(Map<String, String> filterParams);
	
	List<NewLearningContentEntity> fetchSuccesstalks(String sortField, String sortType,
			Map<String, String> filterParams, String search);

	List<NewLearningContentEntity> listPIWs(String region, String sortField, String sortType,
			Map<String, String> query_map, String search);
	
	Integer getSuccessTalkCount();

	HashMap<String, HashMap<String,String>> getViewMoreNewFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCounts);

	Integer getPIWCount();

	Integer getDocumentationCount();

	List<NewLearningContentEntity> fetchRecentlyViewedContent(String userId,  Map<String, String> query_map);

	HashMap<String, HashMap<String, String>> getRecentlyViewedFiltersWithCount(String userId, Map<String, String> query_map,
			HashMap<String, HashMap<String, String>> filterCounts);

	List<NewLearningContentEntity> fetchFilteredContent(String ccoid, Map<String, String> query_map);
	
	HashMap<String, HashMap<String, String>> getBookmarkedFiltersWithCount(Map<String, String> query_map,
			HashMap<String, HashMap<String, String>> filterCounts, List<LearningContentItem> bookmarkedList);
	
	List<NewLearningContentEntity> fetchUpcomingContent(Map<String, String> filterParams);
	
	HashMap<String, HashMap<String,String>> getUpcomingFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCounts);

	List<NewLearningContentEntity> fetchSuccessAcademyContent(Map<String, String> query_map);

	HashMap<String, HashMap<String,String>> getSuccessAcademyFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCounts);

	List<NewLearningContentEntity> fetchCXInsightsContent(Map<String, String> query_map, String searchToken,
			String sortField, String sortType);
	
	LearningMap getLearningMap(String id);

}
