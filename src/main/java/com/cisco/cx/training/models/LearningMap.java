package com.cisco.cx.training.models;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import java.util.List;
import lombok.Data;

@Data
public class LearningMap {

  private String id;

  private String learning_type;

  private String title;

  private String description;

  private String link;

  private String module_count;

  private String date;

  List<LearningModule> learningModules;

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
