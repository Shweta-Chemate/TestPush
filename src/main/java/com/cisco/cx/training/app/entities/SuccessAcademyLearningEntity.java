package com.cisco.cx.training.app.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="success_academy_learnings")
public class SuccessAcademyLearningEntity implements Serializable{

	private static final long serialVersionUID = -3852054556671714530L;
	
	@Id
	@Column(name="row_id")
	private String rowId;
	
	@Column(name="title")
	private String title;
	
	@Column(name="asset_model")
	private String assetModel;
	
	@Column(name="asset_facet")
	private String assetFacet;
	
	@Column(name="asset_group")
	private String assetGroup;
	
	@Column(name="supported_formats")
	private String supportedFormats;
	
	@Column(name="post_date")
	private String postedDt;
	
	@Column(name="description")
	private String description;
	
	@Column(name="learning_link")
	private String learningLink;
	
	@Column(name="last_modified_dt_time")
	private String lastModifiedDtTime;

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

	public String getPostedDt() {
		return postedDt;
	}

	public void setPostedDt(String postedDt) {
		this.postedDt = postedDt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLearningLink() {
		return learningLink;
	}

	public void setLearningLink(String learningLink) {
		this.learningLink = learningLink;
	}

	public String getLastModifiedDtTime() {
		return lastModifiedDtTime;
	}

	public void setLastModifiedDtTime(String lastModifiedDtTime) {
		this.lastModifiedDtTime = lastModifiedDtTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	

	
}
