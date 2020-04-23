package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SuccessAcademyFilter{

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("subfilters")
	private List<String> filters = new ArrayList<>();

	@JsonProperty("tabLocationOnUI")
    private String tabLocationOnUI;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public String getTabLocationOnUI() {
		return tabLocationOnUI;
	}

	public void setTabLocationOnUI(String tabLocationOnUI) {
		this.tabLocationOnUI = tabLocationOnUI;
	}

	

}
