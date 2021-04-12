package com.cisco.cx.training.app.service;

import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import java.util.List;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;

public interface LearningContentService {
	
	SuccessTalkResponseSchema fetchSuccesstalks();

	List<NewLearningContentEntity> fetchPIWs(String region, String sortField, String sortType, String filter,
			String search);

}
