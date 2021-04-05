package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericLearningModel {
	
	@JsonProperty("rowId")
    private String rowId;

	@JsonProperty("title")
    private String title;
	
	@JsonProperty("duration")
    private String duration;

	@JsonProperty("type")
    private String type;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("link")
	private String link;
	
	@JsonProperty("createdTimeStamp")
	private String createdTimeStamp;
	
	@JsonProperty("isBookMarked")
	private Boolean isBookMarked = false;
	
	@JsonProperty("presenterName")
	private String presenterName;

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCreatedTimeStamp() {
		return createdTimeStamp;
	}

	public void setCreatedTimeStamp(String createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}

	public Boolean getIsBookMarked() {
		return isBookMarked;
	}

	public void setIsBookMarked(Boolean isBookMarked) {
		this.isBookMarked = isBookMarked;
	}

	public String getPresenterName() {
		return presenterName;
	}

	public void setPresenterName(String presenterName) {
		this.presenterName = presenterName;
	}
	

}


