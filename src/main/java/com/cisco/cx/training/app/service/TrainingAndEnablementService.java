package com.cisco.cx.training.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.UserLearningPreference;

public interface TrainingAndEnablementService {

	List<SuccessAcademyLearning> getAllSuccessAcademyLearnings(String xMasheryHandshake);		

	List<Community> getAllCommunities();

	CountSchema getCommunityCount();

	List<SuccessAcademyFilter> getSuccessAcademyFilters();
	
	BookmarkResponseSchema bookmarkLearningForUser(BookmarkRequestSchema bookmarkRequestSchema , String xMasheryHandshake, String puid);

	LearningRecordsAndFiltersModel getAllLearningInfoPost(String xMasheryHandshake, String searchToken, HashMap<String, Object> filters,
			String sortBy, String sortOrder, String contentTab);
	
	Map<String, Object> getAllLearningFiltersPost(String searchToken, HashMap<String, Object> filters, String contentTab);

	Map<String, List<UserLearningPreference>> postUserLearningPreferences(String xMasheryHandshake,
			Map<String, List<UserLearningPreference>> userPreferences);

	Map<String, List<UserLearningPreference>> getUserLearningPreferences(String xMasheryHandshake);

	LearningRecordsAndFiltersModel getMyPreferredLearnings(String xMasheryHandshake, String search,
			HashMap<String, Object> filters, String sortBy, String sortOrder, String puid, Integer limit);



}
