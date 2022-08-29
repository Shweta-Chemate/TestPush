package com.cisco.cx.training.models;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
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
