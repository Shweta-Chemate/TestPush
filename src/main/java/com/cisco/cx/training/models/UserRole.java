package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserRole {

  private String roleName;
  private String roleDescription;
  private String roleAbv;
  private int roleId;
  private List<String> resourceList;
}
