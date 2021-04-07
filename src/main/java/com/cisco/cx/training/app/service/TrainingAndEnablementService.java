package com.cisco.cx.training.app.service;

import java.util.HashMap;
import java.util.List;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.cisco.cx.training.models.UserProfile;

public interface TrainingAndEnablementService {

	List<SuccessAcademyLearning> getAllSuccessAcademyLearnings(String xMasheryHandshake);		

	List<Community> getAllCommunities();

	SuccessTalkResponseSchema getAllSuccessTalks();

	SuccessTalkResponseSchema getUserSuccessTalks(String xMasheryHandshake);

	SuccesstalkUserRegEsSchema cancelUserSuccessTalkRegistration(String title, Long eventStartDate, String xMasheryHandshake, String puid) throws Exception;

	SuccesstalkUserRegEsSchema registerUserToSuccessTalkRegistration(String title, Long eventStartDate, String xMasheryHandshake, String puid) throws Exception;

	SuccesstalkUserRegEsSchema fetchSuccessTalkRegistrationDetails(SuccesstalkUserRegEsSchema registration, UserProfile userDetails) throws NotFoundException, NotAllowedException;

	BookmarkResponseSchema createOrUpdateBookmark(BookmarkRequestSchema bookmarkRequestSchema , String xMasheryHandshake);

	CountResponseSchema getIndexCounts();

	CountSchema getCommunityCount();

	CountSchema getSuccessTalkCount();
	
	List<SuccessAcademyFilter> getSuccessAcademyFilters();
	
	BookmarkResponseSchema bookmarkLearningForUser(BookmarkRequestSchema bookmarkRequestSchema , String xMasheryHandshake);
	
	LearningRecordsAndFiltersModel getAllLearningInfo(String xMasheryHandshake);
	
	HashMap<String, Object> getAllLearningFilters();
	
	List<NewLearningContentEntity> fetchNewLearningContent();
}
