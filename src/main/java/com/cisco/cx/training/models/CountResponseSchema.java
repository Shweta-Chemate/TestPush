package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class CountResponseSchema {

  @JsonProperty("learningStatus")
  private List<CountSchema> learningStatus;
}
