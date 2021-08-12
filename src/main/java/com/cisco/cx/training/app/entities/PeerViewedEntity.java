package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "cxpp_peer_viewed_learnings")
@IdClass(PeerViewedEntityPK.class)
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

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRole_name(String roleName) {
		this.roleName = roleName;
	}

	public Timestamp getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Timestamp date) {
		this.updatedTime = date;
	}
	
	
}