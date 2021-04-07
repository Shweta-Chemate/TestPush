package com.cisco.cx.training.app.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "cxpp_learning_content")
public class NewLearningContentEntity {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "learning_type")
	private String learning_type;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "sessionStartDate")
	private Timestamp sessionStartDate;
	
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
	
	@Column(name = "sequence")
	private String sequence;
	
	@Column(name = "published_date")
	private Timestamp published_date;
	
	@Column(name = "modulecount")
	private String modulecount;
	
	@Column(name = "learning_map")
	private String learning_map;
	
	@Column(name = "roles")
	private String roles;
	
	@Column(name = "technology")
	private String technology;
	
	@Column(name = "asset_type")
	private String asset_type;
	
	@Column(name = "link")
	private String link;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Timestamp getPublished_date() {
		return published_date;
	}

	public void setPublished_date(Timestamp published_date) {
		this.published_date = published_date;
	}

	public String getModulecount() {
		return modulecount;
	}

	public void setModulecount(String modulecount) {
		this.modulecount = modulecount;
	}

	public String getLearning_map() {
		return learning_map;
	}

	public void setLearning_map(String learning_map) {
		this.learning_map = learning_map;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getAsset_type() {
		return asset_type;
	}

	public void setAsset_type(String asset_type) {
		this.asset_type = asset_type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}