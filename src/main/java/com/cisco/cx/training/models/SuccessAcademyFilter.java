package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SuccessAcademyFilter {

  @JsonProperty("name")
  private String name;

  @JsonProperty("subfilters")
  private List<String> filters = new ArrayList<>();

  @JsonProperty("tabLocationOnUI")
  private String tabLocationOnUI;
}
