package com.cisco.cx.training.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningMap;

public interface NewLearningContentDAO {
  
	List<NewLearningContentEntity> fetchNewLearningContent(Map<String, List<String>> queryMap, Object stMap, String hcaasStatus);
	
	List<NewLearningContentEntity> fetchSuccesstalks(String sortField, String sortType,
			Map<String, String> filterParams, String search);

	List<NewLearningContentEntity> listPIWs(String region, String sortField, String sortType, //NOSONAR
			Map<String, String> query_map, String search);
	
	Integer getSuccessTalkCount();

	HashMap<String, Object> getViewMoreNewFiltersWithCount(Map<String, Object> filtersSelected, String hcaasStatus);

	Integer getPIWCount();

	Integer getDocumentationCount();
	
	Integer getSuccessTipsCount();

	List<NewLearningContentEntity> fetchRecentlyViewedContent(String userId, Map<String, List<String>> queryMap, Object stMap, String hcaasStatus);

	HashMap<String, Object> getRecentlyViewedFiltersWithCount(String userId, Map<String, Object> filtersSelected, String hcaasStatus);

	List<NewLearningContentEntity> fetchFilteredContent(Map<String, List<String>> queryMap, Object stMap, boolean hcaasStatus);
	
	HashMap<String, Object> getBookmarkedFiltersWithCount(Map<String, Object> filtersSelected, List<LearningContentItem> bookmarkedList, String hcaasStatus);
	
	List<NewLearningContentEntity> fetchUpcomingContent(Map<String, List<String>> queryMap, Object stMap, String hcaasStatus);
	
	HashMap<String, Object> getUpcomingFiltersWithCount(Map<String, Object> filtersSelected, String hcaasStatus);

	List<NewLearningContentEntity> fetchCXInsightsContent(String userId, Map<String, List<String>> queryMap, Object stMap, String searchToken,
			String sortField, String sortType, boolean hcaasStatus);
	
	LearningMap getLearningMap(String id, String title);

	HashMap<String, Object> getCXInsightsFiltersWithCount(String userId, String searchToken, Map<String, Object> filtersSelected, boolean hcaasStatus);

	Integer getSuccessTracksCount();
	
	Integer getLifecycleCount();
	
	Integer getTechnologyCount();
	
	Integer getRolesCount();
	
	Integer getCiscoPlusCount();

	List<NewLearningContentEntity> fetchPopularAcrossPartnersContent(Map<String, List<String>> queryMap, Object stMap, Set<String> userBookmarks, String hcaasStatus);

	HashMap<String, Object> getPopularAcrossPartnersFiltersWithCount(Map<String, Object> filtersSelected, Set<String> userBookmarks, String hcaasStatus);

	List<NewLearningContentEntity> fetchPopularAtPartnerContent(Map<String, List<String>> queryMap, Object stMap,
			String puid, Set<String> userBookmarks, String hcaasStatus);

	HashMap<String, Object> getPopularAtPartnerFiltersWithCount(Map<String, Object> filtersSelected, String puid, Set<String> userBookmarks, String hcaasStatus);
	
	List<NewLearningContentEntity> fetchFeaturedContent(Map<String, List<String>> queryMap, Object stMap, String hcaasStatus);
	
	HashMap<String, Object> getFeaturedFiltersWithCount(Map<String, Object> filtersSelected, String hcaasStatus);

}
