package com.cisco.cx.training.app.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.repo.NewLearningContentRepo;

@Repository
public class NewLearningContentDAOImpl implements NewLearningContentDAO{
	
	@Autowired
	private NewLearningContentRepo learningContentRepo;

	@Override
	public List<NewLearningContentEntity> fetchNewLearningContent() {
		return learningContentRepo.findAll();
	}

}
