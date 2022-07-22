package com.cisco.cx.training.app.service;

import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.LearningRecordsAndFiltersModel;
import com.cisco.cx.training.models.SuccessAcademyFilter;
import com.cisco.cx.training.models.SuccessAcademyLearning;
import com.cisco.cx.training.models.UserLearningPreference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TrainingAndEnablementService {

  List<SuccessAcademyLearning> getAllSuccessAcademyLearnings(String xMasheryHandshake);

  List<Community> getAllCommunities();

  CountSchema getCommunityCount();

  List<SuccessAcademyFilter> getSuccessAcademyFilters();

  BookmarkResponseSchema bookmarkLearningForUser(
      BookmarkRequestSchema bookmarkRequestSchema, String xMasheryHandshake, String puid);

  LearningRecordsAndFiltersModel getAllLearningInfoPost(
      String xMasheryHandshake,
      String searchToken,
      HashMap<String, Object> filters,
      String sortBy,
      String sortOrder,
      String contentTab,
      boolean hcaasStatus);

  Map<String, Object> getAllLearningFiltersPost(
      String searchToken, Map<String, Object> filters, String contentTab, boolean hcaasStatus);

  Map<String, List<UserLearningPreference>> postUserLearningPreferences(
      String xMasheryHandshake, Map<String, List<UserLearningPreference>> userPreferences);

  Map<String, List<UserLearningPreference>> getUserLearningPreferences(String xMasheryHandshake);

  LearningRecordsAndFiltersModel getMyPreferredLearnings(
      String xMasheryHandshake,
      HashMap<String, Object> filters,
      String sortBy,
      Integer limit,
      boolean hcaasStatus);

  Map<String, Object> getTopPicksFiltersPost(Map<String, Object> filters, boolean hcaasStatus);

  LearningRecordsAndFiltersModel getTopPicksCardsPost(
      String xMasheryHandshake, Map<String, Object> filters, boolean hcaasStatus);
}
