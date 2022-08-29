package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserProfile {

  private String userId;
  private String firstName;
  private String lastName;
  private String emailId;
  private String accessLevel;
  private String userTitle;
  private String company;
  private String street;
  private String city;
  private String state;
  private String country;
  private String zipCode;
  private String prefLanguage;
  private String telephone;
  private Set<String> billToIds;
  private String dplAddressFlag;
  private String encryptSwAccess;
}
