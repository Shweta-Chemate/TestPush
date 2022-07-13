package com.cisco.cx.training.models;

import java.util.List;

public class LearningRecordsAndFiltersModel {

  private List<GenericLearningModel> learningData;

  public List<GenericLearningModel> getLearningData() {
    return learningData; // NOSONAR
  }

  public void setLearningData(List<GenericLearningModel> learningData) {
    this.learningData = learningData; // NOSONAR
  }
}
