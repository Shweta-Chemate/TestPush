package com.cisco.cx.training.models;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import java.util.List;

public class LearningMap {

  private String id;

  private String learning_type;

  private String title;

  private String description;

  private String link;

  private String module_count;

  private String date;

  List<LearningModule> learningModules;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLearning_type() {
    return learning_type;
  }

  public void setLearning_type(String learning_type) {
    this.learning_type = learning_type;
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

  public String getModule_count() {
    return module_count;
  }

  public void setModule_count(String module_count) {
    this.module_count = module_count;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<LearningModule> getLearningModules() {
    return learningModules;
  }

  public void setLearningModules(List<LearningModule> learningModules) {
    this.learningModules = learningModules;
  }

  public LearningMap getLearningMapFromEntity(NewLearningContentEntity learningMapEntity) {
    LearningMap learningMap = new LearningMap();
    learningMap.setId(learningMapEntity.getId());
    learningMap.setTitle(learningMapEntity.getTitle());
    learningMap.setDescription(learningMapEntity.getDescription());
    learningMap.setLearning_type(learningMapEntity.getLearningType());
    learningMap.setDate(
        learningMapEntity.getSortByDate() != null
            ? learningMapEntity.getSortByDate().toInstant().toString()
            : null);
    learningMap.setLink(learningMapEntity.getLink());
    learningMap.setModule_count(learningMapEntity.getModulecount());
    return learningMap;
  }
}
