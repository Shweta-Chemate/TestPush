package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class UserDetails {

  private String cecId;
  private String country;
  private String email;
  private String firstName;
  private String lastName;
  private String company;
  private String phone;
  private String picture;
  private String role;
  private String title;
  private String level;
  private static final String COMMA = " ,";
  private String street;
  private String city;
  private String state;
  private String zipcode;

  public String getAddress() {
    StringBuilder address = new StringBuilder();
    if (this.street != null) {
      address.append(this.street);
    }
    if (this.city != null) {
      address.append(COMMA + this.city);
    }
    if (this.state != null) {
      address.append(COMMA + this.state);
    }
    if (this.country != null) {
      address.append(COMMA + this.country);
    }
    if (this.zipcode != null) {
      address.append(COMMA + this.zipcode);
    }
    return address.toString();
  }

  @JsonAlias({"userId"})
  public String getCecId() {
    return cecId;
  }

  @JsonAlias({"emailId"})
  public String getEmail() {
    return email;
  }

  @JsonAlias({"telephone"})
  public String getPhone() {
    return phone;
  }

  @JsonAlias({"userTitle"})
  public String getTitle() {
    return title;
  }
}
