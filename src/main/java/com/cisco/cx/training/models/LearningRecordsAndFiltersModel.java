package com.cisco.cx.training.models;

import java.util.HashMap;
import java.util.List;

public class LearningRecordsAndFiltersModel {
	
	private List<GenericLearningModel> learningData;	
	
	public List<GenericLearningModel> getLearningData() {
		return learningData;
	}
	public void setLearningData(List<GenericLearningModel> learningData) {
		this.learningData = learningData;
	}
	
}
