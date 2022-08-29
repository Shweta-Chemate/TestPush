package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SuccessAcademyLearning {

  @JsonProperty("rowId")
  private String rowId;

  @JsonProperty("title")
  private String title;

  @JsonProperty("assetModel")
  private String assetModel;

  @JsonProperty("assetFacet")
  private String assetFacet;

  @JsonProperty("assetGroup")
  private String assetGroup;

  @JsonProperty("supportedFormats")
  private String supportedFormats;

  @JsonProperty("postDate")
  private String postDate;

  @JsonProperty("description")
  private String description;

  @JsonProperty("link")
  private String link;

  @JsonProperty("isBookMarked")
  private Boolean isBookMarked = false;
}
