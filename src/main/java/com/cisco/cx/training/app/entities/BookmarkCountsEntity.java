package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "cxpp_learning_status")
@IdClass(LearningStatusEntityPK.class)
public class BookmarkCountsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "puid")
	private String puid;

	@Id
	@Column(name = "learning_item_id")
	private String learningItemId;

	@Column(name = "count")
	private Integer count;

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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
