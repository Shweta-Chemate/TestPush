package com.cisco.cx.training.app.dao;

import java.util.List;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;

public interface NewLearningContentDAO {
	
	List<NewLearningContentEntity> fetchNewLearningContent();

}
