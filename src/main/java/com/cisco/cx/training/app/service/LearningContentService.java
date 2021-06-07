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
	
	Map<String, Map<String,String>> getViewMoreNewFiltersWithCount(String filter, HashMap<String, HashMap<String,String>> filterCounts);

	LearningStatusEntity updateUserStatus(String userId, String puid, LearningStatusSchema learningStatusSchema, String xMasheryHandshake);

	List<LearningContentItem> fetchRecentlyViewedContent(String userId, String filter);

	Map<String, Map<String,String>> getRecentlyViewedFiltersWithCount(String userId, String filter,
			HashMap<String, HashMap<String, String>> filterCounts);

	List<LearningContentItem> fetchBookMarkedContent(String userId, String filter);
	
	Map<String, Map<String,String>> getBookmarkedFiltersWithCount(String ccoid, String filter,
			HashMap<String, HashMap<String, String>> filterCounts);
	
	List<LearningContentItem> fetchUpcomingContent(String ccoid, String filter);
	
	Map<String, Map<String,String>> getUpcomingFiltersWithCount(String filter, HashMap<String, HashMap<String,String>> filterCounts);

	List<LearningContentItem> fetchSuccessAcademyContent(String userId, String filter);

	Map<String, Map<String,String>> getSuccessAcademyFiltersWithCount(String filter, HashMap<String, HashMap<String,String>> filterCounts);

	List<LearningContentItem> fetchCXInsightsContent(String userId, String filter, String searchToken, String sortField,
			String sortType);

	LearningMap getLearningMap(String id);
}
