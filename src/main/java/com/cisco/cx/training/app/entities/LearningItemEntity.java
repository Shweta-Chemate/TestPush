package com.cisco.cx.training.app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cxpp_learning_item")
// @SQLDelete(sql="UPDATE cxpp_learning_item SET deleted=true where learning_type='PIW' AND
// learning_item_id=?")
public class LearningItemEntity {

  @Id
  @Column(name = "learning_item_id")
  private String learning_item_id;

  @Column(name = "learning_type")
  private String learning_type;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "status")
  private String status;

  @Column(name = "registrationurl")
  private String registrationUrl;

  @Column(name = "presentername")
  private String presenterName;

  @Column(name = "recordingurl")
  private String recordingUrl;

  @Column(name = "duration")
  private String duration;

  @Column(name = "piw_region")
  private String piw_region;

  @Column(name = "piw_score")
  private Integer piw_score;

  @Column(name = "piw_language")
  private String piw_language;

  @Column(name = "sort_by_date")
  private String sortByDate;

  @Column(name = "avg_rating_percentage")
  private Integer avgRatingPercentage;

  @Column(name = "total_completions")
  private Integer totalCompletions;

  @Column(name = "votes_percentage")
  private Integer votesPercentage;

  @Column(name = "specialization")
  private String specialization;

  private String asset_types;
  private String asset_links;
  private String learning_map;
  private String asset_description;
  private String asset_titles;

  public String getLearning_item_id() {
    return learning_item_id;
  }

  public void setLearning_item_id(String learning_item_id) {
    this.learning_item_id = learning_item_id;
  }

  public String getLearning_type() {
    return learning_type;
  }

  public void setLearning_type(String learning_type) {
    this.learning_type = learning_type;
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

  public String getRegistrationUrl() {
    return registrationUrl;
  }

  public void setRegistrationUrl(String registrationUrl) {
    this.registrationUrl = registrationUrl;
  }

  public String getPresenterName() {
    return presenterName;
  }

  public void setPresenterName(String presenterName) {
    this.presenterName = presenterName;
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

  public String getPiw_region() {
    return piw_region;
  }

  public void setPiw_region(String piw_region) {
    this.piw_region = piw_region;
  }

  public Integer getPiw_score() {
    return piw_score;
  }

  public void setPiw_score(Integer piw_score) {
    this.piw_score = piw_score;
  }

  public String getPiw_language() {
    return piw_language;
  }

  public void setPiw_language(String piw_language) {
    this.piw_language = piw_language;
  }

  public String getSortByDate() {
    return sortByDate;
  }

  public void setSortByDate(String sortByDate) {
    this.sortByDate = sortByDate;
  }

  public String getAsset_types() {
    return asset_types;
  }

  public void setAsset_types(String asset_types) {
    this.asset_types = asset_types;
  }

  public String getAsset_links() {
    return asset_links;
  }

  public void setAsset_links(String asset_links) {
    this.asset_links = asset_links;
  }

  public String getLearning_map() {
    return learning_map;
  }

  public void setLearning_map(String learning_map) {
    this.learning_map = learning_map;
  }

  public Integer getAvgRatingPercentage() {
    return avgRatingPercentage;
  }

  public void setAvgRatingPercentage(Integer avgRatingPercentage) {
    this.avgRatingPercentage = avgRatingPercentage;
  }

  public Integer getTotalCompletions() {
    return totalCompletions;
  }

  public void setTotalCompletions(Integer totalCompletions) {
    this.totalCompletions = totalCompletions;
  }

  public Integer getVotesPercentage() {
    return votesPercentage;
  }

  public void setVotesPercentage(Integer votesPercentage) {
    this.votesPercentage = votesPercentage;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public String getAsset_description() {
    return asset_description;
  }

  public void setAsset_description(String asset_description) {
    this.asset_description = asset_description;
  }

  public String getAsset_titles() {
    return asset_titles;
  }

  public void setAsset_titles(String asset_titles) {
    this.asset_titles = asset_titles;
  }
}
