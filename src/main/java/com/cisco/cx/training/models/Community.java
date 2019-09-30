package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;

public class Community implements HasId{

	private String solution;
    private String usecase;
	private String name; 
	private String url;
    private String description;
    private String docId;
    
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String getDocId() {
		// TODO Auto-generated method stub
		return docId;
	}
	@Override
	public void setDocId(String id) {
		this.docId = id;
		
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