package com.cisco.cx.training.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessTalkResponseSchema {

	@JsonProperty("solution")
	private String solution = "";

	@JsonProperty("usecase")
	private String usecase = "";

	@JsonProperty("sucessTalks")
	private List<SuccessTalk> items;

	public List<SuccessTalk> getItems() {
		return items;
	}

	public void setItems(List<SuccessTalk> items) {
		this.items = items;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getUsecase() {
		return usecase;
	}

	public void setUsecase(String usecase) {
		this.usecase = usecase;
	}
}
