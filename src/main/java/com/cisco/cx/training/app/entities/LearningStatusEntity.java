package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "cxpp_learning_status")
@IdClass(LearningStatusEntityPK.class)
public class LearningStatusEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "user_id")
  private String userId;

  @Id
  @Column(name = "puid")
  private String puid;

  @Id
  @Column(name = "learning_item_id")
  private String learningItemId;

  @Column(name = "reg_status")
  private String regStatus;

  @Column(name = "reg_updated_timestamp")
  private Timestamp regUpdatedTimestamp;

  @Column(name = "viewed_timestamp")
  private Timestamp viewedTimestamp;

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

  public String getRegStatus() {
    return regStatus;
  }

  public void setRegStatus(String regStatus) {
    this.regStatus = regStatus;
  }

  public Timestamp getRegUpdatedTimestamp() {
    return regUpdatedTimestamp;
  }

  public void setRegUpdatedTimestamp(Timestamp regUpdatedTimestamp) {
    this.regUpdatedTimestamp = regUpdatedTimestamp;
  }

  public Timestamp getViewedTimestamp() {
    return viewedTimestamp;
  }

  public void setViewedTimestamp(Timestamp viewedTimestamp) {
    this.viewedTimestamp = viewedTimestamp;
  }
}
