package com.cisco.cx.training.app.dao;

import java.util.List;
import java.util.Map;
import com.cisco.cx.training.models.UserLearningPreference;

public interface UserLearningPreferencesDAO {

	Map<String, List<UserLearningPreference>> createOrUpdateULP(String userId, Map<String, List<UserLearningPreference>> ulPreferences);

	Map<String, List<UserLearningPreference>> fetchUserLearningPreferences(String userId);

}