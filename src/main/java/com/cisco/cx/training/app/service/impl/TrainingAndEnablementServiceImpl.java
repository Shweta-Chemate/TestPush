package com.cisco.cx.training.app.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.*;

@Service
public class TrainingAndEnablementServiceImpl implements TrainingAndEnablementService {
	
	@Autowired
	private CommunityDAO communityDAO;
	
	@Autowired
	private SuccessTalkDAO successTalkDAO;

	@Override
	public SuccessTrackAndUseCases getUsecases() {
		Map<String, List<String>> useCases = new HashMap<String, List<String>>();
		useCases.put("IBN", new ArrayList<>(Arrays.asList("Campus Network Assurance", "Network Device Onboarding",
				"Campus Software Image management", "Campus Network Segmentation", "Scalable Access Policy")));
		SuccessTrackAndUseCases successTrackAndUseCases = new SuccessTrackAndUseCases();
		successTrackAndUseCases.setUseCases(useCases);
		return successTrackAndUseCases;
	}

	@Override
	public List<LearningModel> getLearning() {
		Learning learning = new Learning();
		learning.setUrl("https://salesconnect.cisco.com/#/");
		learning.setDescription("");

		LearningModel eLearning = new LearningModel();
		eLearning.setName("E-Learning");
		eLearning.setLearning(Arrays.asList(learning));

		return Arrays.asList(eLearning);
	}

	@Override
	public Community insertCommunity(Community community) {
		return communityDAO.insertCommunity(community);
	}

	@Override
	public List<Community> getAllCommunities() {
		return communityDAO.getCommunities();
	}

	@Override
	public List<Community> getFilteredCommunities(String solution, String usecase) {
		return communityDAO.getFilteredCommunities(solution, usecase);
	}
	
	@Override
	public SuccessTalk insertSuccessTalk(SuccessTalk successTalk) {
		return successTalkDAO.insertSuccessTalk(successTalk);
	}

	@Override
	public List<SuccessTalk> getAllSuccessTalks() {
		return successTalkDAO.getAllSuccessTalks();
	}

	@Override
	public SuccessTalkResponseSchema getFilteredSuccessTalks(String solution, String usecase) {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getFilteredSuccessTalks(solution, usecase));
		successTalkResponseSchema.setSolution(solution);
		successTalkResponseSchema.setUsecase(usecase);
		return successTalkResponseSchema;
	}
}