package com.cisco.cx.training.models;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessAcademyFilterMap {

	@JsonProperty("key")
	private String key;

	@JsonProperty("values")
	private Set<String> values;
	
	@JsonProperty("displayType")
    private String displayType;
	
	@JsonProperty("showFilters")
    private String showFilters;
	
	@JsonProperty("tabLocationOnUI")
    private String tabLocationOnUI;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}

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

	public String getTabLocationOnUI() {
		return tabLocationOnUI;
	}

	public void setTabLocationOnUI(String tabLocationOnUI) {
		this.tabLocationOnUI = tabLocationOnUI;
	}
	
	

}
