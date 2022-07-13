package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Company {

  private String partnerName;
  private String partnerId;
  private String puid;
  private String domainIdentifier;
  private boolean demoAccount;
  private boolean hcaas;
  private List<UserRole> roleList;

  public String getPartnerName() {
    return partnerName;
  }

  public void setPartnerName(String partnerName) {
    this.partnerName = partnerName;
  }

  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public String getPuid() {
    return puid;
  }

  public void setPuid(String puid) {
    this.puid = puid;
  }

  public String getDomainIdentifier() {
    return domainIdentifier;
  }

  public void setDomainIdentifier(String domainIdentifier) {
    this.domainIdentifier = domainIdentifier;
  }

  public List<UserRole> getRoleList() {
    return roleList; // NOSONAR
  }

  public void setRoleList(List<UserRole> roleList) {
    this.roleList = roleList; // NOSONAR
  }

  public boolean isDemoAccount() {
    return demoAccount;
  }

  public void setDemoAccount(boolean demoAccount) {
    this.demoAccount = demoAccount;
  }

  public boolean isHcaas() {
    return hcaas;
  }

  public void setHcaas(boolean hcaas) {
    this.hcaas = hcaas;
  }

  @Override
  public String toString() {
    return "Company [partnerName="
        + partnerName
        + ", partnerId="
        + partnerId
        + ", puid="
        + puid
        + ", domainIdentifier="
        + domainIdentifier
        + ", demoAccount="
        + demoAccount
        + ", hcaas="
        + hcaas
        + ", roleList="
        + roleList
        + "]";
  }
}
