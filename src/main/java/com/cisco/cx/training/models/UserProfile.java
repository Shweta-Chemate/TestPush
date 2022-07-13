package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }

  public String getAccessLevel() {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel) {
    this.accessLevel = accessLevel;
  }

  public String getUserTitle() {
    return userTitle;
  }

  public void setUserTitle(String userTitle) {
    this.userTitle = userTitle;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getPrefLanguage() {
    return prefLanguage;
  }

  public void setPrefLanguage(String prefLanguage) {
    this.prefLanguage = prefLanguage;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public Set<String> getBillToIds() {
    return billToIds; // NOSONAR
  }

  public void setBillToIds(Set<String> billToIds) {
    this.billToIds = billToIds; // NOSONAR
  }

  public String getDplAddressFlag() {
    return dplAddressFlag;
  }

  public void setDplAddressFlag(String dplAddressFlag) {
    this.dplAddressFlag = dplAddressFlag;
  }

  public String getEncryptSwAccess() {
    return encryptSwAccess;
  }

  public void setEncryptSwAccess(String encryptSwAccess) {
    this.encryptSwAccess = encryptSwAccess;
  }
}
