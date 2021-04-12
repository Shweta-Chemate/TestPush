package com.cisco.cx.training.app.service;

import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import java.util.List;
import java.util.Map;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;

public interface LearningContentService {

	SuccessTalkResponseSchema fetchSuccesstalks(String sortField, String sortType, String filter, String search);

	List<NewLearningContentEntity> fetchPIWs(String region, String sortField, String sortType, String filter,
			String search);

	CountResponseSchema getIndexCounts();

}
