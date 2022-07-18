package com.cisco.cx.training.models;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LearningStatusSchema {

  @NotNull(message = "cannot be null")
  private String learningItemId;

  private Registration regStatus;

  private boolean viewed;

  @Override
  public String toString() {
    return "LearningStatusSchema [learning_item_id="
        + learningItemId
        + ", bookmarkStatus="
        + regStatus
        + ", viewed"
        + viewed
        + "]";
  }

  public enum Registration {
    REGISTERED_T,
    CANCELLED_T
  }
}
