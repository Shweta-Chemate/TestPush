package com.cisco.cx.training.app.dao;

import java.util.List;

import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.LearningModel;

public interface LearningDAO {

	List<LearningModel> getLearnings();

	Learning insertLearning(Learning learning);

	List<LearningModel> getFilteredLearnings(String solution, String usecase);

}
