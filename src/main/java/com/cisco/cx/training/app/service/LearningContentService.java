package com.cisco.cx.training.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningMap;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.PIW;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;

public interface LearningContentService {

	SuccessTalkResponseSchema fetchSuccesstalks(String ccoId, String sortField, String sortType, String filter, String search);

	List<PIW> fetchPIWs(String ccoId, String region, String sortField, String sortType, String filter,
			String search);

	CountResponseSchema getIndexCounts();
	
	List<LearningContentItem> fetchNewLearningContent(String ccoId, HashMap<String, Object> filtersSelected);

	Map<String, Object> getViewMoreNewFiltersWithCount(HashMap<String, Object> filtersSelected);

	LearningStatusEntity updateUserStatus(String userId, String puid, LearningStatusSchema learningStatusSchema, String xMasheryHandshake);

	List<LearningContentItem> fetchRecentlyViewedContent(String userId, HashMap<String, Object> filtersSelected);

	Map<String, Object> getRecentlyViewedFiltersWithCount(String userId, HashMap<String, Object> filtersSelected);

	List<LearningContentItem> fetchBookMarkedContent(String userId, HashMap<String, Object> filtersSelected);
	
	Map<String, Object> getBookmarkedFiltersWithCount(String ccoid, HashMap<String, Object> filtersSelected);
	
	List<LearningContentItem> fetchUpcomingContent(String userId, HashMap<String, Object> filtersSelected);
	
	Map<String, Object> getUpcomingFiltersWithCount(HashMap<String, Object> filtersSelected);

	List<LearningContentItem> fetchCXInsightsContent(String userId, HashMap<String, Object> filtersSelected, String searchToken, String sortField,
			String sortType);

	Map<String, Object> getCXInsightsFiltersWithCount(String userId, String searchToken, HashMap<String, Object> filtersSelected);

	LearningMap getLearningMap(String id, String title);
}
