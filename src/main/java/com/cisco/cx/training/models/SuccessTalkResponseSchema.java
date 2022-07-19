package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class SuccessTalkResponseSchema {

  @JsonProperty("solution")
  private String solution = "";

  @JsonProperty("usecase")
  private String usecase = "";

  @JsonProperty("sessionDetails")
  private List<SuccessTalk> items;
}
