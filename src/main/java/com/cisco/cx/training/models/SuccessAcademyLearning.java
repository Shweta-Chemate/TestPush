package com.cisco.cx.training.models;

import java.util.ArrayList;
import java.util.List;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessAcademyLearning{
	

	@JsonProperty("title")
    private String title;
	
	@JsonProperty("assetModel")
	private String assetModel;
	
	@JsonProperty("assetFacet")
	private String assetFacet;
	
	@JsonProperty("assetGroup")
	private String assetGroup;
	
	@JsonProperty("supportedFormats")
	private String supportedFormats;
	
	@JsonProperty("postDate")
	private String postDate;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("link")
	private String link;
	
	@JsonProperty("isBookMarked")
	private Boolean isBookMarked = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getSupportedFormats() {
		return supportedFormats;
	}

	public void setSupportedFormats(String supportedFormats) {
		this.supportedFormats = supportedFormats;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
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

	public Boolean getIsBookMarked() {
		return isBookMarked;
	}

	public void setIsBookMarked(Boolean isBookMarked) {
		this.isBookMarked = isBookMarked;
	}
	
	
	
	
	
	
}
