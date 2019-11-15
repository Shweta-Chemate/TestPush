package com.cisco.cx.training.models;

import java.util.List;
import java.util.Set;

public class SuccessAcademyModel {

	private String name;

	private String displayType;

	private String showFilters;

	private String tabLocationOnUI;

	private Set<String> solutionTypes;

	private List<SuccessAcademyLearning> learningDetails;

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getShowFilters() {
		return showFilters;
	}

	public void setShowFilters(String showFilters) {
		this.showFilters = showFilters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SuccessAcademyLearning> getLearningDetails() {
		return learningDetails;
	}

	public void setLearningDetails(List<SuccessAcademyLearning> learningDetails) {
		this.learningDetails = learningDetails;
	}

	public String getTabLocationOnUI() {
		return tabLocationOnUI;
	}

	public void setTabLocationOnUI(String tabLocationOnUI) {
		this.tabLocationOnUI = tabLocationOnUI;
	}

	public Set<String> getSolutionTypes() {
		return solutionTypes;
	}

	public void setSolutionTypes(Set<String> solutionTypes) {
		this.solutionTypes = solutionTypes;
	}
}