package com.cisco.cx.training.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;

public class LearningContentItem {
	
	public LearningContentItem(NewLearningContentEntity entity) {
		this.id=entity.getId();
		this.learningType=entity.getLearningType();
		this.title=entity.getTitle();
		this.description=entity.getDescription();
		this.sortByDate=entity.getSortByDate();
		this.status=entity.getStatus();
		this.registrationUrl=entity.getRegistrationUrl();
		this.presenterName=entity.getPresenterName();
		this.recordingUrl=entity.getRecordingUrl();
		this.duration=entity.getDuration();
		this.region=entity.getRegion();
		this.piw_architecture=entity.getPiw_architecture();
		this.piw_technology=entity.getPiw_technology();
		this.piw_sub_technology=entity.getPiw_sub_technology();
		this.piw_score=entity.getPiw_score();
		this.language=entity.getLanguage();
		this.piw_timezone=entity.getPiw_timezone();
		this.piw_ppt_url=entity.getPiw_ppt_url();
		this.sequence=entity.getSequence();
		this.published_date=entity.getPublished_date();
		this.created_date=entity.getCreated_date();
		this.updated_date=entity.getUpdated_date();
		this.modulecount=entity.getModulecount();
		this.learning_map=entity.getLearning_map();
		this.roles=entity.getRoles();
		this.technology=entity.getTechnology();
		this.contentType=entity.getContentType();
		this.link=entity.getLink();
		this.archetype=entity.getArchetype();
		this.assetFacet=entity.getAssetFacet();
		this.assetGroup=entity.getAssetGroup();
		this.assetModel=entity.getAssetModel();
	}

	private String id;

	private String learningType;
	
	private String title;
	
	private String description;
	
	private Timestamp sessionStartDate;
	
	private Timestamp sortByDate;
	
	private String status;
	
	private LocalDateTime regTimestamp;
	
	private Boolean bookmark;
	
	private String registrationUrl;
	
	private String presenterName;
	
	private String recordingUrl;
	
	private String duration;
	
	private String region;
	
	private String piw_architecture;
	
	private String piw_technology;
	
	private String piw_sub_technology;
	
	private Integer piw_score;
	
	private String language;
	
	private String piw_timezone;
	
	private String piw_ppt_url;
	
	private String sequence;
	
	private Timestamp published_date;
	
	private Timestamp created_date;

	private Timestamp updated_date;
	
	private String modulecount;
	
	private String learning_map;
	
	private String roles;
	
	private String technology;
	
	private String contentType;
	
	private String link;
	
	private String archetype;

	private String assetModel;

	private String assetFacet;

	private String assetGroup;

	private long bookmarkTimeStamp;

	public long getBookmarkTimeStamp() {
		return bookmarkTimeStamp;
	}

	public void setBookmarkTimeStamp(long bookmarkTimeStamp) {
		this.bookmarkTimeStamp = bookmarkTimeStamp;
	}

	public String getArchetype() {
		return archetype;
	}

	public void setArchetype(String archetype) {
		this.archetype = archetype;
	}

	public String getAssetModel() {
		return assetModel;
	}

	public void setAssetModel(String assetModel) {
		this.assetModel = assetModel;
	}

	public String getAssetFacet() {
		return assetFacet;
	}

	public void setAssetFacet(String assetFacet) {
		this.assetFacet = assetFacet;
	}

	public String getAssetGroup() {
		return assetGroup;
	}

	public void setAssetGroup(String assetGroup) {
		this.assetGroup = assetGroup;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getBookmark() {
		return bookmark;
	}

	public void setBookmark(Boolean bookmark) {
		this.bookmark = bookmark;
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

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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
	
	public LocalDateTime getRegTimestamp() {
		return regTimestamp;
	}

	public void setRegTimestamp(LocalDateTime regTimestamp) {
		this.regTimestamp = regTimestamp;
	}

}
