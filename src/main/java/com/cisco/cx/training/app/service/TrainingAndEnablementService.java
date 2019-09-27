package com.cisco.cx.training.app.service;

import java.util.List;

import com.cisco.cx.training.models.SuccessTrackAndUseCases;
import com.cisco.cx.training.models.*;

public interface TrainingAndEnablementService {

	SuccessTrackAndUseCases getUsecases();

	List<Community> getCommunities();

	List<LearningModel> getLearning();

}
