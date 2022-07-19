package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BookmarkRequestSchema {

  @JsonIgnore
  public boolean isNotBlank() {
    return StringUtils.isNotBlank(title);
  }

  @ApiModelProperty(notes = "Unique Identifier of the selected Success Talk")
  private String id;

  @ApiModelProperty(notes = "Title", example = "New Customer Experience Specialization overview")
  private String title;

  @ApiModelProperty(notes = "RowId of the CX Learning item")
  private String learningid;

  @ApiModelProperty(notes = "is Bookmarked", example = "true | false")
  private boolean bookmark = false;

  @Override
  public int hashCode() {
    return Objects.hash(getTitle());
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  public String getId() {
    this.id = String.valueOf(this.hashCode());
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
