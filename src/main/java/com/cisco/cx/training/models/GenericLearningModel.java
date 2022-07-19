package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GenericLearningModel {

  @JsonProperty("id")
  private String id; // for TP UI

  @JsonProperty("rowId")
  private String rowId;

  @JsonProperty("title")
  private String title;

  @JsonProperty("duration")
  private String duration;

  @JsonProperty("type")
  private String type;

  @JsonProperty("description")
  private String description;

  @JsonProperty("link")
  private String link;

  @JsonProperty("createdTimeStamp")
  private String createdTimeStamp;

  @JsonProperty("isBookMarked")
  private Boolean isBookMarked = false;

  @JsonProperty("presenterName")
  private String presenterName;

  @JsonProperty("status")
  private String status;

  @JsonProperty("rating")
  private Integer rating;

  @JsonProperty("specialization")
  private String specialization;

  private String registrationUrl;
  private String recordingUrl;
  private String contentType;
  private String urlDescrption;

  private String modulecount;
  private String learning_map;

  private Integer avgRatingPercentage;
  private Integer votesPercentage;
  private Integer totalCompletions;

  private List<SuccessTipsAttachment> successTipsVideos;

  private List<SuccessTipsAttachment> successTipsFiles;
}
