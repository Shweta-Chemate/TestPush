package com.cisco.cx.training.app.service;

import java.util.List;

import com.cisco.cx.training.models.*;

public interface TrainingAndEnablementService {

	SuccessTrackAndUseCases getUsecases();

	List<LearningModel> getAllLearning();
	
	Learning insertLearning(Learning learning);
		
	List<LearningModel> getFilteredLearning(String solution, String usecase);

	Community insertCommunity(Community community);

	List<Community> getAllCommunities();

	List<Community> getFilteredCommunities(String solution, String usecase);

	SuccessTalk insertSuccessTalk(SuccessTalk successTalk);

	SuccessTalkResponseSchema getAllSuccessTalks();

	SuccessTalkResponseSchema getFilteredSuccessTalks(String solution, String usecase);
	
    public String registerUserToSuccessTalkSession(String sessionId, String successTalkId);
    
    public String cancelUserToSuccessTalkSession(String sessionId, String successTalkId);
}
