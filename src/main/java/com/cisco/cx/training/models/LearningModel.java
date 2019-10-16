package com.cisco.cx.training.models;

import java.util.List;
import java.util.Set;

public class LearningModel {
	String name;
	
	Set<String> categoryTypes;

	List<Learning> learning;	

	public Set<String> getCategoryTypes() {
		return categoryTypes;
	}

	public void setCategoryTypes(Set<String> categoryTypes) {
		this.categoryTypes = categoryTypes;
	}

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