package com.cisco.cx.training.models;

import javax.validation.constraints.NotNull;

public class LearningStatusSchema {

  @NotNull(message = "cannot be null")
  private String learningItemId;

  private Registration regStatus;

  private boolean viewed;

  public String getLearningItemId() {
    return learningItemId;
  }

  public void setLearningItemId(String learningItemId) {
    this.learningItemId = learningItemId;
  }

  public Registration getRegStatus() {
    return regStatus;
  }

  public void setRegStatus(Registration regStatus) {
    this.regStatus = regStatus;
  }

  public boolean isViewed() {
    return viewed;
  }

  public void setViewed(boolean viewed) {
    this.viewed = viewed;
  }

  @Override
  public String toString() {
    return "LearningStatusSchema [learning_item_id="
        + learningItemId
        + ", bookmarkStatus="
        + regStatus
        + ", viewed"
        + viewed
        + "]";
  }

  public enum Registration {
    REGISTERED_T,
    CANCELLED_T
  }
}
