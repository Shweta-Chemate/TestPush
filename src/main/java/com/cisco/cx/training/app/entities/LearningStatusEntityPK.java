package com.cisco.cx.training.app.entities;

import java.io.Serializable;

public class LearningStatusEntityPK implements Serializable {

  private static final long serialVersionUID = 1L;

  private String userId;

  private String puid;

  private String learningItemId;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getPuid() {
    return puid;
  }

  public void setPuid(String puid) {
    this.puid = puid;
  }

  public String getLearningItemId() {
    return learningItemId;
  }

  public void setLearningItemId(String learningItemId) {
    this.learningItemId = learningItemId;
  }
}
