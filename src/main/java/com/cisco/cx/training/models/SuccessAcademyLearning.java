package com.cisco.cx.training.models;

import java.util.ArrayList;
import java.util.List;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessAcademyLearning implements HasId{
	

	@JsonProperty("name")
    private String name;
	
	@JsonProperty("parentFilter")
    private String parentFilter;
	
	@JsonProperty("trainingColour")
    private String trainingColour;
	
	@JsonProperty("url")
    private String url;
	
	@JsonProperty("description")
    private String description;
	
	@JsonProperty("docId")
    private String docId;
	
	@JsonProperty("img")
    private String imgLoc;	
	
	@JsonProperty("learning")
    private List<SuccessAcademyLearningTopics> learning = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentFilter() {
		return parentFilter;
	}

	public void setParentFilter(String parentFilter) {
		this.parentFilter = parentFilter;
	}

	public String getTrainingColour() {
		return trainingColour;
	}

	public void setTrainingColour(String trainingColour) {
		this.trainingColour = trainingColour;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SuccessAcademyLearningTopics> getLearning() {
		return learning;
	}

	public void setLearning(List<SuccessAcademyLearningTopics> learning) {
		this.learning = learning;
	}

	@Override
	public String getDocId() {
		
		return docId;
	}

	@Override
	public void setDocId(String id) {
		this.docId = id;
		
	}

	public String getImgLoc() {
		return imgLoc;
	}

	public void setImgLoc(String imgLoc) {
		this.imgLoc = imgLoc;
	}

}
