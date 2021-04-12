package com.cisco.cx.training.app.dao;

import java.util.List;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.models.SuccessTalk;

public interface NewLearningContentDAO {
	
	List<NewLearningContentEntity> fetchNewLearningContent();
	
	List<NewLearningContentEntity> fetchSuccesstalks();
}
