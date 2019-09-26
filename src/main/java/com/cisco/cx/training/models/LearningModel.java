package com.cisco.cx.training.models;

import java.util.List;

public class LearningModel {
	String name;

	List<Learning> learning;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Learning> getLearning() {
		return learning;
	}

	public void setLearning(List<Learning> learning) {
		this.learning = learning;
	}
}