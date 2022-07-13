package com.cisco.cx.training.util;

import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;
import com.cisco.cx.training.models.SuccessAcademyLearning;

public class SuccessAcademyMapper {

  public static SuccessAcademyLearning getLearningsFromEntity(SuccessAcademyLearningEntity entity) {
    SuccessAcademyLearning learning = null;
    if (null != entity) {
      learning = new SuccessAcademyLearning();
      learning.setAssetFacet(entity.getAssetFacet());
      learning.setAssetGroup(entity.getAssetGroup());
      learning.setAssetModel(entity.getAssetModel());
      learning.setDescription(entity.getDescription());
      learning.setRowId(entity.getRowId());
      learning.setLink(entity.getLearningLink());
      learning.setPostDate(entity.getPostedDt());
      learning.setSupportedFormats(entity.getSupportedFormats());
      learning.setTitle(entity.getTitle());
    }
    return learning;
  }
}
