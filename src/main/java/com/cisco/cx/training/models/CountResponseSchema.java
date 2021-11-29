package com.cisco.cx.training.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountResponseSchema {

	@JsonProperty("learningStatus")
	private List<CountSchema> learningStatus;

	public List<CountSchema> getLearningStatus() {
		return learningStatus; //NOSONAR
	}

	public void setLearningStatus(List<CountSchema> learningStatus) {
		this.learningStatus = learningStatus; //NOSONAR
	}
}
