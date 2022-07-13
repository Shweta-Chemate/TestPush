package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CountResponseSchema {

  @JsonProperty("learningStatus")
  private List<CountSchema> learningStatus;

  public List<CountSchema> getLearningStatus() {
    return learningStatus; // NOSONAR
  }

  public void setLearningStatus(List<CountSchema> learningStatus) {
    this.learningStatus = learningStatus; // NOSONAR
  }
}
