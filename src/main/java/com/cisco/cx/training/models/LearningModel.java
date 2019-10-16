package com.cisco.cx.training.models;

import java.util.List;
import java.util.Set;

public class LearningModel {
	String name;
	
	Set<String> solutionTypes;

	List<Learning> learning;	

	public Set<String> getSolutionTypes() {
		return solutionTypes;
	}

	public void setSolutionTypes(Set<String> solutionTypes) {
		this.solutionTypes = solutionTypes;
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