package com.cisco.cx.training.app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cxpp_learning_item")
// @SQLDelete(sql="UPDATE cxpp_learning_item SET deleted=true where learning_type='PIW' AND
// learning_item_id=?")
@Data
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
}
