package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SuccessTalkResponseSchema {

  @JsonProperty("solution")
  private String solution = "";

  @JsonProperty("usecase")
  private String usecase = "";

  @JsonProperty("sessionDetails")
  private List<SuccessTalk> items;

  public List<SuccessTalk> getItems() {
    return items; // NOSONAR
  }

  public void setItems(List<SuccessTalk> items) {
    this.items = items; // NOSONAR
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
