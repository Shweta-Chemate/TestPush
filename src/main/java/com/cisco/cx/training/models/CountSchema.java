package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountSchema {

  @ApiModelProperty(notes = "Label of Index", example = "Communities")
  private String label;

  @ApiModelProperty(notes = "Total Count", example = "15")
  private int count;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = Math.toIntExact(count);
  }
}
