package com.cisco.cx.training.app.dao;

import com.cisco.cx.training.models.UserLearningPreference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserLearningPreferencesDAO {

  Map<String, List<UserLearningPreference>> createOrUpdateULP(
      String userId, Map<String, List<UserLearningPreference>> ulPreferences);

  Map<String, List<UserLearningPreference>> fetchUserLearningPreferences(String userId);

  HashMap<String, Object> getULPPreferencesDDB(String userId);
}
