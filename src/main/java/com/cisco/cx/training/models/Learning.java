package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;

public class Learning implements HasId{

	String name;
 	String url;
 	long timeDuration;
 	int star;
 	long timeCompleted;
	String category;
	String usecase;
	String solution;
	String status;
	String description;
	private String docId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimeDuration() {
		return timeDuration;
	}

	public void setTimeDuration(long timeDuration) {
		this.timeDuration = timeDuration;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public long getTimeCompleted() {
		return timeCompleted;
	}

	public void setTimeCompleted(long timeCompleted) {
		this.timeCompleted = timeCompleted;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUsecase() {
		return usecase;
	}

	public void setUsecase(String usecase) {
		this.usecase = usecase;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	
}
