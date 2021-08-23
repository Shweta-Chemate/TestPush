package com.cisco.cx.training.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningMap;

public interface NewLearningContentDAO {
  
	List<NewLearningContentEntity> fetchNewLearningContent( Map<String, List<String>> queryMap, Object stMap);
	
	List<NewLearningContentEntity> fetchSuccesstalks(String sortField, String sortType,
			Map<String, String> filterParams, String search);

	List<NewLearningContentEntity> listPIWs(String region, String sortField, String sortType,
			Map<String, String> query_map, String search);
	
	Integer getSuccessTalkCount();

	HashMap<String, Object> getViewMoreNewFiltersWithCount(HashMap<String, Object> filtersSelected);

	Integer getPIWCount();

	Integer getDocumentationCount();

	List<NewLearningContentEntity> fetchRecentlyViewedContent(String userId, Map<String, List<String>> queryMap, Object stMap);

	HashMap<String, Object> getRecentlyViewedFiltersWithCount(String userId, HashMap<String, Object> filtersSelected);

	List<NewLearningContentEntity> fetchFilteredContent(Map<String, List<String>> queryMap, Object stMap);
	
	HashMap<String, Object> getBookmarkedFiltersWithCount(HashMap<String, Object> filtersSelected, List<LearningContentItem> bookmarkedList);
	
	List<NewLearningContentEntity> fetchUpcomingContent(Map<String, List<String>> queryMap, Object stMap);
	
	HashMap<String, Object> getUpcomingFiltersWithCount(HashMap<String, Object> filtersSelected);

	List<NewLearningContentEntity> fetchCXInsightsContent(String userId, Map<String, List<String>> queryMap, Object stMap, String searchToken,
			String sortField, String sortType);
	
	LearningMap getLearningMap(String id, String title);

	HashMap<String, Object> getCXInsightsFiltersWithCount(String userId, String searchToken, HashMap<String, Object> filtersSelected);

	Integer getSuccessTracksCount();
	
	Integer getLifecycleCount();
	
	Integer getTechnologyCount();
	
	Integer getRolesCount();

	List<NewLearningContentEntity> fetchPopularAcrossPartnersContent(Map<String, List<String>> queryMap, Object stMap);

	HashMap<String, Object> getPopularAcrossPartnersFiltersWithCount(HashMap<String, Object> filtersSelected);

	List<NewLearningContentEntity> fetchPopularAtPartnerContent(Map<String, List<String>> queryMap, Object stMap,
			String puid);

	HashMap<String, Object> getPopularAtPartnerFiltersWithCount(HashMap<String, Object> filtersSelected, String puid);
}
