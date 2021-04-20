package com.cisco.cx.training.app.service;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import com.cisco.cx.training.app.entities.LearningStatusEntity;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.LearningContentItem;
import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.PIW;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;

public interface LearningContentService {

	SuccessTalkResponseSchema fetchSuccesstalks(String ccoId, String sortField, String sortType, String filter, String search);

	List<PIW> fetchPIWs(String ccoId, String region, String sortField, String sortType, String filter,
			String search);

	CountResponseSchema getIndexCounts();
	
	HashMap<String, HashMap<String,String>> getViewMoreFiltersWithCount(String filter, HashMap<String, HashMap<String,String>> filterCounts);

	LearningStatusEntity updateUserStatus(String userId, String puid, LearningStatusSchema learningStatusSchema, String xMasheryHandshake);

	List<LearningContentItem> fetchRecentlyViewedContent(String puid, String userId, String filter);

	HashMap<String, HashMap<String, String>> getRecentlyViewedFiltersWithCount(String puid,String userId, String filter,
			HashMap<String, HashMap<String, String>> filterCounts);

}
