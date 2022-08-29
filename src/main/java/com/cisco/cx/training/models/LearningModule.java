package com.cisco.cx.training.models;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import lombok.Data;

@Data
public class LearningModule {

  private String id;

  private String title;

  private String description;

  private String link;

  private String asset_type;

  private int sequence;

  public LearningModule getLearningModuleFromEntity(NewLearningContentEntity learningModuleEntity) {
    LearningModule learningModule = new LearningModule();
    learningModule.setId(learningModuleEntity.getId());
    learningModule.setTitle(learningModuleEntity.getTitle());
    learningModule.setDescription(learningModuleEntity.getDescription());
    if (learningModuleEntity.getSequence() != null) {
      learningModule.setSequence(Integer.parseInt(learningModuleEntity.getSequence()));
    }
    learningModule.setAsset_type(learningModuleEntity.getContentType());
    learningModule.setLink(learningModuleEntity.getLink());
    return learningModule;
  }
}
