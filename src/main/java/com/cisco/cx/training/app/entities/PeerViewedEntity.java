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
@Table(name = "cxpp_peer_viewed_learnings")
@IdClass(PeerViewedEntityPK.class)
@Data
public class PeerViewedEntity implements Serializable {

  private static final long serialVersionUID = -4058359459722185774L;

  @Id
  @Column(name = "card_id")
  private String cardId;

  @Id
  @Column(name = "role_name")
  private String roleName;

  @Column(name = "updated_time")
  private Timestamp updatedTime;
}
