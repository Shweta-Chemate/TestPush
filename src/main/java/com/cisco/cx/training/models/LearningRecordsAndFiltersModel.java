package com.cisco.cx.training.models;

import java.util.HashMap;
import java.util.List;

public class LearningRecordsAndFiltersModel {
	
	private List<GenericLearningModel> learningData;	
	private HashMap<String, Object> filters;
	
	
	public List<GenericLearningModel> getLearningData() {
		return learningData;
	}
	public void setLearningData(List<GenericLearningModel> learningData) {
		this.learningData = learningData;
	}
	public HashMap<String, Object> getFilters() {
		return filters;
	}
	public void setFilters(HashMap<String, Object> filters) {
		this.filters = filters;
	}
	
	
}
