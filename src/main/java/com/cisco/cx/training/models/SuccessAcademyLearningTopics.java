package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessAcademyLearningTopics {
	
	@JsonProperty("name")
    private String name;
	
	@JsonProperty("description")
    private String description;
	
	@JsonProperty("link")
    private String link;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
