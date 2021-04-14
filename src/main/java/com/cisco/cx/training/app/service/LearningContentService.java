package com.cisco.cx.training.app.service;

import java.util.HashMap;
import java.util.List;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;

public interface LearningContentService {

	SuccessTalkResponseSchema fetchSuccesstalks(String sortField, String sortType, String filter, String search);

	List<NewLearningContentEntity> fetchPIWs(String region, String sortField, String sortType, String filter,
			String search);

	CountResponseSchema getIndexCounts();
	
	HashMap<String, Object> getViewMoreFiltersWithCount(String filter);

}
