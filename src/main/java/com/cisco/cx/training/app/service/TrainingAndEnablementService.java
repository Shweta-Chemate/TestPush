package com.cisco.cx.training.app.service;

import java.util.List;

import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.models.*;

public interface TrainingAndEnablementService {

	List<LearningModel> getAllLearning();
	
	Learning insertLearning(Learning learning);
		
	List<LearningModel> getFilteredLearning(String solution, String usecase);

	List<Community> getAllCommunities();

	SuccessTalk insertSuccessTalk(SuccessTalk successTalk);

	SuccessTalkResponseSchema getAllSuccessTalks();

	SuccessTalkResponseSchema getUserSuccessTalks(String email);

	SuccesstalkUserRegEsSchema cancelUserSuccessTalkRegistration(String title, Long eventStartDate, String email) throws Exception;

	SuccesstalkUserRegEsSchema registerUserToSuccessTalkRegistration(String title, Long eventStartDate, String email) throws Exception;

	SuccesstalkUserRegEsSchema fetchSuccessTalkRegistrationDetails(SuccesstalkUserRegEsSchema registration) throws NotFoundException, NotAllowedException;

	BookmarkResponseSchema createOrUpdateBookmark(BookmarkRequestSchema bookmarkRequestSchema, String email);
}
