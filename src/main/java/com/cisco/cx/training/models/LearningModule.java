package com.cisco.cx.training.models;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;

public class LearningModule {

	private String id;

	private String title;

	private String description;

	private String link;

	private String asset_type;

	private int sequence;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAsset_type() {
		return asset_type;
	}

	public void setAsset_type(String asset_type) {
		this.asset_type = asset_type;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public LearningModule getLearningModuleFromEntity(NewLearningContentEntity learningModuleEntity) {
		LearningModule learningModule = new LearningModule();
		learningModule.setId(learningModuleEntity.getId());
		learningModule.setTitle(learningModuleEntity.getTitle());
		learningModule.setDescription(learningModuleEntity.getDescription());
		if(learningModuleEntity.getSequence()!=null)
		learningModule.setSequence(Integer.parseInt(learningModuleEntity.getSequence()));
		learningModule.setAsset_type(learningModuleEntity.getContentType());
		learningModule.setLink(learningModuleEntity.getLink());
		return learningModule;
	}

}
