package com.cisco.cx.training.models;

import lombok.Data;

@Data
public class PIWSession {

  private String sessionId;
  private String sessionStartDate;
  private String presenterName;
  private String registrationUrl;
  private Boolean scheduled;
}
