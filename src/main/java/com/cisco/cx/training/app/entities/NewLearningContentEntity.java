package com.cisco.cx.training.app.entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cxpp_learning_content")
@Data
public class NewLearningContentEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "learning_type")
  private String learningType;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "sessionStartDate")
  private Timestamp sessionStartDate;

  @Column(name = "sort_by_date")
  private Timestamp sortByDate;

  @Column(name = "status")
  private String status;

  @Column(name = "registrationUrl")
  private String registrationUrl;

  @Column(name = "presenterName")
  private String presenterName;

  @Column(name = "recordingUrl")
  private String recordingUrl;

  @Column(name = "duration")
  private String duration;

  @Column(name = "piw_region")
  private String region;

  @Column(name = "piw_architecture")
  private String piw_architecture;

  @Column(name = "piw_technology")
  private String piw_technology;

  @Column(name = "piw_sub_technology")
  private String piw_sub_technology;

  @Column(name = "piw_score")
  private Integer piw_score;

  @Column(name = "piw_language")
  private String language;

  @Column(name = "piw_timezone")
  private String piw_timezone;

  @Column(name = "piw_ppt_url")
  private String piw_ppt_url;

  @Column(name = "sequence")
  private String sequence;

  @Column(name = "published_date")
  private Timestamp published_date;

  @Column(name = "created_date")
  private Timestamp created_date;

  @Column(name = "updated_date")
  private Timestamp updated_date;

  @Column(name = "modulecount")
  private String modulecount;

  @Column(name = "learning_map")
  private String learningMap;

  @Column(name = "roles")
  private String roles;

  @Column(name = "technology")
  private String technology;

  @Column(name = "asset_type")
  private String contentType;

  @Column(name = "link")
  private String link;

  @Column(name = "archetype")
  private String archetype;

  @Column(name = "avg_rating_percentage")
  private Integer avgRatingPercentage;

  @Column(name = "total_completions")
  private Integer totalCompletions;

  @Column(name = "votes_percentage")
  private Integer votesPercentage;

  @Column(name = "ciscoplus")
  private String ciscoplus;

  @Column(name = "link_title")
  private String link_title;

  @Column(name = "link_description")
  private String link_descrption;
}
