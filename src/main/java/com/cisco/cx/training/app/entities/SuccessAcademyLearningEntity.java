package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cxpp_success_academy_learnings")
@Data
public class SuccessAcademyLearningEntity implements Serializable {

  private static final long serialVersionUID = -3852054556671714530L;

  @Id
  @Column(name = "row_id")
  private String rowId;

  @Column(name = "title")
  private String title;

  @Column(name = "asset_model")
  private String assetModel;

  @Column(name = "asset_facet")
  private String assetFacet;

  @Column(name = "asset_group")
  private String assetGroup;

  @Column(name = "supported_formats")
  private String supportedFormats;

  @Column(name = "post_date")
  private String postedDt;

  @Column(name = "description")
  private String description;

  @Column(name = "learning_link")
  private String learningLink;

  @Column(name = "last_modified_dt_time")
  private String lastModifiedDtTime;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
