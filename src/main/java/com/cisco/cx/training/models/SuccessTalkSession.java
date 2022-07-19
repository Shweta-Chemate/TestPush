package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SuccessTalkSession implements HasId {

  private String sessionId;
  private String sessionStartDate;
  private String presenterName;
  private String registrationUrl;
  @JsonIgnore private String region;
  private Boolean scheduled;

  @Override
  public String getDocId() {
    return sessionId;
  }

  @Override
  public void setDocId(String id) {
    this.sessionId = id;
  }
}
