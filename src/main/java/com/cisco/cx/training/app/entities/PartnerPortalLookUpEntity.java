package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cxpp_lookup")
@Data
public class PartnerPortalLookUpEntity implements Serializable {

  /** */
  private static final long serialVersionUID = -7610906142767752144L;

  @Id
  @Column(name = "id")
  private String rowId;

  @Column(name = "cxpp_key")
  private String partnerPortalKey;

  @Column(name = "cxpp_value")
  private String partnerPortalKeyValue;

  @Column(name = "description")
  private String description;

  @Column(name = "created_dt_time")
  private Date createdDtTime;

  @Column(name = "updated_dt_time")
  private Date updatedDtTime;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;
}
