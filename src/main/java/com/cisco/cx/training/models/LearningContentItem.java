package com.cisco.cx.training.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.constants.Constants;

public class LearningContentItem {
	
	public LearningContentItem(NewLearningContentEntity entity) {
		this.id=entity.getId();
		this.learningType=entity.getLearningType();
		this.title=entity.getTitle();
		this.description=entity.getDescription();
		this.sortByDate=entity.getSortByDate()!=null?entity.getSortByDate().toInstant().toString():null;
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
		this.published_date=entity.getPublished_date()!=null?entity.getPublished_date().toInstant().toString():null;
		this.created_date=entity.getCreated_date()!=null?entity.getCreated_date().toInstant().toString():null;
		this.updated_date=entity.getUpdated_date()!=null?entity.getCreated_date().toInstant().toString():null;
		this.modulecount=entity.getModulecount();
		this.learning_map=entity.getLearningMap();
		this.roles=entity.getRoles();
		this.technology=entity.getTechnology();
		this.contentType=entity.getContentType();
		this.link=entity.getLink();
		this.archetype=entity.getArchetype();
		this.avgRatingPercentage=entity.getAvgRatingPercentage();
		this.totalCompletions=entity.getTotalCompletions();
		this.votesPercentage=entity.getVotesPercentage();
		this.link_title = entity.getLink_title();
		this.link_description = entity.getLink_descrption();
		populateSuccessTipsData();
		
	}

	private String id;

	private String learningType;
	
	private String title;
	
	private String description;
	
	private String sessionStartDate;
	
	private String sortByDate;
	
	private String status;
	
	private String regTimestamp;
	
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
	
	private String published_date;
	
	private String created_date;

	private String updated_date;
	
	private String modulecount;
	
	private String learning_map;
	
	private String roles;
	
	private String technology;
	
	private String contentType;
	
	private String link;
	
	private String archetype;

	private String bookmarkTimeStamp;

	private Integer avgRatingPercentage;

	private Integer totalCompletions;

	private Integer votesPercentage;
	
	private String link_title;
	
	private String link_description;
	
	private List<SuccessTipsAttachment> successTipsVideos;
	
	private List<SuccessTipsAttachment> successTipsFiles;

	public Integer getTotalCompletions() {
		return totalCompletions;
	}

	public void setTotalCompletions(Integer totalCompletions) {
		this.totalCompletions = totalCompletions;
	}

	public Integer getAvgRatingPercentage() {
		return avgRatingPercentage;
	}

	public void setAvgRatingPercentage(Integer avgRatingPercentage) {
		this.avgRatingPercentage = avgRatingPercentage;
	}

	public Integer getVotesPercentage() {
		return votesPercentage;
	}

	public void setVotesPercentage(Integer votesPercentage) {
		this.votesPercentage = votesPercentage;
	}

	public String getBookmarkTimeStamp() {
		return bookmarkTimeStamp;
	}

	public void setBookmarkTimeStamp(String bookmarkTimeStamp) {
		this.bookmarkTimeStamp = bookmarkTimeStamp;
	}

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

	public String getSessionStartDate() {
		return sessionStartDate;
	}

	public void setSessionStartDate(String sessionStartDate) {
		this.sessionStartDate = sessionStartDate;
	}

	public String getSortByDate() {
		return sortByDate;
	}

	public void setSortByDate(String sortByDate) {
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

	public String getPublished_date() {
		return published_date;
	}

	public void setPublished_date(String published_date) {
		this.published_date = published_date;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(String updated_date) {
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
	
	public String getRegTimestamp() {
		return regTimestamp;
	}

	public void setRegTimestamp(String regTimestamp) {
		this.regTimestamp = regTimestamp;
	}

	public List<SuccessTipsAttachment> getSuccessTipsVideos() {
		return successTipsVideos;
	}

	public void setSuccessTipsVideos(List<SuccessTipsAttachment> successTipsVideos) {
		this.successTipsVideos = successTipsVideos;
	}

	public List<SuccessTipsAttachment> getSuccessTipsFiles() {
		return successTipsFiles;
	}

	public void setSuccessTipsFiles(List<SuccessTipsAttachment> successTipsFiles) {
		this.successTipsFiles = successTipsFiles;
	}
	
	private void populateSuccessTipsData() {
		if(Constants.SUCCESSTIPS.equalsIgnoreCase(this.learningType)) {
			List<SuccessTipsAttachment> videoAttachments = new ArrayList<>();
			List<SuccessTipsAttachment> fileAttachments = new ArrayList<>();
			String[] asset_types = this.contentType.split(",");
			String[] asset_links = this.link.split(",");
			String[] asset_description = new String[0];
			if(StringUtils.isNotBlank(link_description)) {
				asset_description = this.link_description.split(":");
			}
			String[] asset_titles = new String[0];
			if(StringUtils.isNotBlank(link_title)) {
				asset_titles = this.link_title.split(":");
			}
			for(int i=0 ;i<asset_types.length;i++) {
				SuccessTipsAttachment successTipAttac = new SuccessTipsAttachment();
				successTipAttac.setAttachmentType(asset_types[i]);
				successTipAttac.setUrl(asset_links[i]);
				if(asset_description.length == 0) {
					successTipAttac.setUrlDescription("");
				}else {
					successTipAttac.setUrlDescription(asset_description[i]);
				}
				if(asset_titles.length == 0) {
					successTipAttac.setUrlTitle("");
				}else {
					successTipAttac.setUrlTitle(asset_titles[i]);
				}
				if(asset_types[i].equalsIgnoreCase(Constants.SUCCESS_TIPS_VIDEO)) {
					videoAttachments.add(successTipAttac);
				}else {
					fileAttachments.add(successTipAttac);
				}
			}
			if(!videoAttachments.isEmpty()) {
				this.setSuccessTipsVideos(videoAttachments);
			}
			if(!fileAttachments.isEmpty()) {
				this.setSuccessTipsFiles(fileAttachments);
			}
		}
	}

}
