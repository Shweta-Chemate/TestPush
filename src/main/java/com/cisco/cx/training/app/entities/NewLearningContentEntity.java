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

	public String getArchetype() {
		return archetype;
	}

	public void setArchetype(String archetype) {
		this.archetype = archetype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLearningType() {
		return learningType;
	}

	public void setLearningType(String learningType) {
		this.learningType = learningType;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public String getLearningMap() {
		return learningMap;
	}

	public void setLearningMap(String learningMap) {
		this.learningMap = learningMap;
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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Timestamp getSessionStartDate() {
		return sessionStartDate;
	}

	public void setSessionStartDate(Timestamp sessionStartDate) {
		this.sessionStartDate = sessionStartDate;
	}

	public Timestamp getSortByDate() {
		return sortByDate;
	}

	public void setSortByDate(Timestamp sortByDate) {
		this.sortByDate = sortByDate;
	}

	public Timestamp getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Timestamp created_date) {
		this.created_date = created_date;
	}

	public Timestamp getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Timestamp updated_date) {
		this.updated_date = updated_date;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
}