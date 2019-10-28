package com.cisco.cx.training.app.service;

import java.util.List;

import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
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
	
	SuccessTalkResponseSchema getUserSuccessTalks(String email);

	SuccesstalkUserRegEsSchema cancelUserSuccessTalkRegistration(String title, String email) throws Exception;

	SuccesstalkUserRegEsSchema registerUserToSuccessTalkRegistration(String title, String email) throws Exception;

	SuccesstalkUserRegEsSchema fetchSuccessTalkRegistrationDetails(SuccesstalkUserRegEsSchema registration)
			throws NotFoundException, NotAllowedException;

	BookmarkResponseSchema createOrUpdateBookmark(BookmarkRequestSchema bookmarkRequestSchema, String email);
}
