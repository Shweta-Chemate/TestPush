package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserDetailsWithCompanyList {

  private UserProfile ciscoUserProfileSchema;
  private List<Company> companyList;

  @Override
  public String toString() {
    return "UserDetailsWithCompanyList [ciscoUserProfileSchema="
        + ciscoUserProfileSchema
        + ", companyList="
        + companyList
        + "]";
  }
}
