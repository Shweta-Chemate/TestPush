package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SuccessAcademyFilter implements HasId {

	@JsonProperty("filters")
	private List<SuccessAcademyFilterMap> filters = new ArrayList<>();

	@JsonProperty("docId")
	private String docId;

	public List<SuccessAcademyFilterMap> getFilters() {
		return filters;
	}

	public void setFilters(List<SuccessAcademyFilterMap> filters) {
		this.filters = filters;
	}

	@Override
	public String getDocId() {
		// TODO Auto-generated method stub
		return docId;
	}

	@Override
	public void setDocId(String id) {
		docId = id;

	}

}
