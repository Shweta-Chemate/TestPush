package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class UserLearningPreference {

  @JsonProperty("name")
  private String name;

  @JsonProperty("selected")
  private boolean selected = false;

  @JsonProperty("timeMap")
  private Map<String, String> timeMap;

  @Override
  public String toString() {
    String toString = "Preference [name=" + name + ", selected =" + selected;
    if (null != timeMap) {
      for (String key : timeMap.keySet()) {
        toString += " key=" + key + " value=" + timeMap.get(key) + ",";
      }
    }
    toString += "]";
    return toString;
  }
}
