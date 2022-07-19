package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cxpp_learning_status")
@IdClass(LearningStatusEntityPK.class)
@Data
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
}
