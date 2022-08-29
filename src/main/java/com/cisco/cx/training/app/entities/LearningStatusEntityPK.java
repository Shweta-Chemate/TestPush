package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import lombok.Data;

@Data
public class LearningStatusEntityPK implements Serializable {

  private static final long serialVersionUID = 1L;

  private String userId;

  private String puid;

  private String learningItemId;
}
