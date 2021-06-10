package com.cisco.cx.training.app.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "cxpp_learning_item")
//@SQLDelete(sql="UPDATE cxpp_learning_item SET deleted=true where learning_type='PIW' AND learning_item_id=?")
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
	
	@Column(name = "sessionstartdate")
	private Timestamp sessionStartDate;
	
	@UpdateTimestamp
	@Column(name = "updated_timestamp")
	private Timestamp updated_timestamp;
	
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
	
	@Column(name = "piw_architecture")
	private String piw_architecture;
	
	@Column(name = "piw_technology")
	private String piw_technology;
	
	@Column(name = "piw_sub_technology")
	private String piw_sub_technology;
	
	@Column(name = "piw_score")
	private Integer piw_score;
	
	@Column(name = "piw_language")
	private String piw_language;
	
	@Column(name = "piw_timezone")
	private String piw_timezone;
	
	@Column(name = "piw_ppt_url")
	private String piw_ppt_url;
	
	@Column(name = "sort_by_date")
	private String sortByDate;
	
	@Column(name = "deleted")
	private boolean deleted;

	
	private String asset_types;	
	private String asset_links;
	private String learning_map;

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

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

	public Timestamp getSessionStartDate() {
		return sessionStartDate;
	}

	public void setSessionStartDate(Timestamp sessionStartDate) {
		this.sessionStartDate = sessionStartDate;
	}

	public Timestamp getUpdated_timestamp() {
		return updated_timestamp;
	}

	public void setUpdated_timestamp(Timestamp updated_timestamp) {
		this.updated_timestamp = updated_timestamp;
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

	public String getPiw_architecture() {
		return piw_architecture;
	}

	public void setPiw_architecture(String piw_architecture) {
		this.piw_architecture = piw_architecture;
	}

	public String getPiw_technology() {
		return piw_technology;
	}

	public void setPiw_technology(String piw_technology) {
		this.piw_technology = piw_technology;
	}

	public String getPiw_sub_technology() {
		return piw_sub_technology;
	}

	public void setPiw_sub_technology(String piw_sub_technology) {
		this.piw_sub_technology = piw_sub_technology;
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

	public String getPiw_timezone() {
		return piw_timezone;
	}

	public void setPiw_timezone(String piw_timezone) {
		this.piw_timezone = piw_timezone;
	}

	public String getPiw_ppt_url() {
		return piw_ppt_url;
	}

	public void setPiw_ppt_url(String piw_ppt_url) {
		this.piw_ppt_url = piw_ppt_url;
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

}
