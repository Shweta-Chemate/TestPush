package com.cisco.cx.training.app.service;

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

	CountResponseSchema getIndexCounts(boolean hcaasStatus);
	
	List<LearningContentItem> fetchNewLearningContent(String ccoId, Map<String, Object> filtersSelected, boolean hcaasStatus);

	Map<String, Object> getViewMoreNewFiltersWithCount(Map<String, Object> filtersSelected, boolean hcaasStatus);

	LearningStatusEntity updateUserStatus(String userId, String puid, LearningStatusSchema learningStatusSchema, String xMasheryHandshake);

	List<LearningContentItem> fetchRecentlyViewedContent(String userId, Map<String, Object> filtersSelected, boolean hcaasStatus);

	Map<String, Object> getRecentlyViewedFiltersWithCount(String userId, Map<String, Object> filtersSelected, boolean hcaasStatus);

	List<LearningContentItem> fetchBookMarkedContent(String userId, Map<String, Object> filtersSelected, boolean hcaasStatus);
	
	Map<String, Object> getBookmarkedFiltersWithCount(String ccoid, Map<String, Object> filtersSelected, boolean hcaasStatus);
	
	List<LearningContentItem> fetchUpcomingContent(String userId, Map<String, Object> filtersSelected, boolean hcaasStatus);
	
	Map<String, Object> getUpcomingFiltersWithCount(Map<String, Object> filtersSelected, boolean hcaasStatus);

	List<LearningContentItem> fetchCXInsightsContent(String userId, Map<String, Object> filtersSelected, String searchToken, String sortField,
			String sortType, boolean hcaasStatus);

	Map<String, Object> getCXInsightsFiltersWithCount(String userId, String searchToken, Map<String, Object> filtersSelected, boolean hcaasStatus);

	LearningMap getLearningMap(String id, String title);

	List<LearningContentItem> fetchPopularContent(String ccoid, Map<String, Object> filtersSelected, String popularityType, String puid, boolean hcaasStatus);

	Map<String, Object> getPopularContentFiltersWithCount(Map<String, Object> filtersSelected, String puid, String popularityType, String userId, boolean hcaasStatus);
	
	List<LearningContentItem> fetchFeaturedContent(String userId, Map<String, Object> filtersSelected, boolean hcaasStatus);
	
	Map<String, Object> getFeaturedFiltersWithCount(Map<String, Object> filtersSelected, boolean hcaasStatus);
}
