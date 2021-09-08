package com.cisco.cx.training.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.cx.training.models.LearningStatusSchema;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;
import com.cisco.cx.training.models.UserLearningPreference;

import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public interface UserLearningPreferencesDAO {

	Map<String, List<UserLearningPreference>> createOrUpdateULP(String userId, Map<String, List<UserLearningPreference>> ulPreferences);

	Map<String, List<UserLearningPreference>> fetchUserLearningPreferences(String userId);

	HashMap<String, Object> getULPPreferencesDDB(String userId);	

}