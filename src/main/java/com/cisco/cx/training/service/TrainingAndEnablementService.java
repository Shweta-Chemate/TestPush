package com.cisco.cx.training.service;

import java.util.List;

import com.cisco.cx.training.models.SuccessTrackAndUseCases;
import com.cisco.cx.training.models.Community;

public interface TrainingAndEnablementService {

	SuccessTrackAndUseCases getUsecases();

	List<Community> getCommunities();

}
