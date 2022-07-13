package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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

  public Integer getAvgRatingPercentage() {
    return avgRatingPercentage;
  }

  public void setAvgRatingPercentage(Integer avgRatingPercentage) {
    this.avgRatingPercentage = avgRatingPercentage;
  }

  public Integer getVotesPercentage() {
    return votesPercentage;
  }

  public void setVotesPercentage(Integer votesPercentage) {
    this.votesPercentage = votesPercentage;
  }

  public Integer getTotalCompletions() {
    return totalCompletions;
  }

  public void setTotalCompletions(Integer totalCompletions) {
    this.totalCompletions = totalCompletions;
  }

  public String getRowId() {
    return rowId;
  }

  public void setRowId(String rowId) {
    this.rowId = rowId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getCreatedTimeStamp() {
    return createdTimeStamp;
  }

  public void setCreatedTimeStamp(String createdTimeStamp) {
    this.createdTimeStamp = createdTimeStamp;
  }

  public Boolean getIsBookMarked() {
    return isBookMarked;
  }

  public void setIsBookMarked(Boolean isBookMarked) {
    this.isBookMarked = isBookMarked;
  }

  public String getPresenterName() {
    return presenterName;
  }

  public void setPresenterName(String presenterName) {
    this.presenterName = presenterName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getRegistrationUrl() {
    return registrationUrl;
  }

  public void setRegistrationUrl(String registrationUrl) {
    this.registrationUrl = registrationUrl;
  }

  public String getRecordingUrl() {
    return recordingUrl;
  }

  public void setRecordingUrl(String recordingUrl) {
    this.recordingUrl = recordingUrl;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getModulecount() {
    return modulecount;
  }

  public void setModulecount(String modulecount) {
    this.modulecount = modulecount;
  }

  public String getLearning_map() {
    return learning_map;
  }

  public void setLearning_map(String learning_map) {
    this.learning_map = learning_map;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public List<SuccessTipsAttachment> getSuccessTipsVideos() {
    return successTipsVideos;
  }

  public void setSuccessTipsVideos(List<SuccessTipsAttachment> successTipsVideos) {
    this.successTipsVideos = successTipsVideos;
  }

  public List<SuccessTipsAttachment> getSuccessTipsFiles() {
    return successTipsFiles;
  }

  public void setSuccessTipsFiles(List<SuccessTipsAttachment> successTipsFiles) {
    this.successTipsFiles = successTipsFiles;
  }

  public String getUrlDescrption() {
    return urlDescrption;
  }

  public void setUrlDescrption(String urlDescrption) {
    this.urlDescrption = urlDescrption;
  }
}
