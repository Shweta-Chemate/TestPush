package com.cisco.cx.training.models;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;

public class PIW {

  private String piwId;

  private String title;

  private String description;

  private String status;

  private String regTimestamp;

  private boolean bookmark;

  private String recordingUrl;

  private String duration;

  private String region;

  private String architecture;

  private Integer score;

  private String technology;

  private String sub_technology;

  private String timezone;

  private String language;

  private String ppt_url;

  private List<PIWSession> sessions;

  public String getPiwId() {
    return piwId;
  }

  public void setPiwId(String piwId) {
    this.piwId = piwId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getRecordingUrl() {
    return recordingUrl;
  }

  public void setRecordingUrl(String recordingUrl) {
    this.recordingUrl = recordingUrl;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getArchitecture() {
    return architecture;
  }

  public void setArchitecture(String architecture) {
    this.architecture = architecture;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public String getTechnology() {
    return technology;
  }

  public void setTechnology(String technology) {
    this.technology = technology;
  }

  public String getSub_technology() {
    return sub_technology;
  }

  public void setSub_technology(String sub_technology) {
    this.sub_technology = sub_technology;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getPpt_url() {
    return ppt_url;
  }

  public void setPpt_url(String ppt_url) {
    this.ppt_url = ppt_url;
  }

  public List<PIWSession> getSessions() {
    return sessions; // NOSONAR
  }

  public void setSessions(List<PIWSession> sessions) {
    this.sessions = sessions; // NOSONAR
  }

  public boolean isBookmark() {
    return bookmark;
  }

  public void setBookmark(boolean bookmark) {
    this.bookmark = bookmark;
  }

  public String getRegTimestamp() {
    return regTimestamp;
  }

  public void setRegTimestamp(String regTimestamp) {
    this.regTimestamp = regTimestamp;
  }

  public enum PIWStatusEnum {
    ATTENDED("Attended"),

    REGISTERED_AND_ATTENDED("Registered and attended"),

    RECOMMENDED("Recommended"),

    REQUESTED("Requested"),

    REGISTERED("Registered"),

    CONFIRMED("Confirmed"),

    PROPOSED("Proposed"),

    CANCELLED("Cancelled");

    private String value;

    PIWStatusEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PIW.PIWStatusEnum fromValue(String text) {
      for (PIW.PIWStatusEnum b : PIW.PIWStatusEnum.values()) {
        if (String.valueOf(b.value).equalsIgnoreCase(text)) {
          return b;
        }
      }
      return null;
    }
  }

  public PIW(NewLearningContentEntity item) {
    this.piwId = item.getId();
    this.title = item.getTitle();
    this.description = item.getDescription();
    this.status = item.getStatus();
    this.recordingUrl = item.getRecordingUrl();
    this.duration = item.getDuration();
    this.region = item.getRegion();
    this.architecture = item.getPiw_architecture();
    this.score = item.getPiw_score();
    this.technology = item.getPiw_technology();
    this.sub_technology = item.getPiw_sub_technology();
    this.timezone = item.getPiw_timezone();
    this.language = item.getLanguage();
    this.ppt_url = item.getPiw_ppt_url();
    PIWSession session = new PIWSession();
    session.setSessionId(item.getId());
    session.setRegistrationUrl(item.getRegistrationUrl());
    session.setSessionStartDate(
        item.getSessionStartDate() != null
            ? item.getSessionStartDate().toInstant().toString()
            : null);
    session.setPresenterName(item.getPresenterName());
    session.setScheduled(false);
    List<PIWSession> sessions = new ArrayList<>();
    sessions.add(session);
    this.setSessions(sessions); // NOSONAR
  }
}
