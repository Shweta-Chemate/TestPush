package com.cisco.cx.training.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;

public interface NewLearningContentDAO {
  
	List<NewLearningContentEntity> fetchNewLearningContent(Map<String, String> filterParams);
	
	List<NewLearningContentEntity> fetchSuccesstalks(String sortField, String sortType,
			Map<String, String> filterParams, String search);

	List<NewLearningContentEntity> listPIWs(String region, String sortField, String sortType,
			Map<String, String> query_map, String search);
	
	Integer getSuccessTalkCount();

	HashMap<String, HashMap<String,String>> getViewMoreFiltersWithCount(Map<String, String> filter, HashMap<String, HashMap<String,String>> filterCounts);

	Integer getPIWCount();

	Integer getDocumentationCount();

	List<NewLearningContentEntity> fetchRecentlyViewedContent(String puid, String userId,  Map<String, String> query_map);

	HashMap<String, HashMap<String, String>> getRecentlyViewedFiltersWithCount(String puid, String userId, Map<String, String> query_map,
			HashMap<String, HashMap<String, String>> filterCounts);
}
