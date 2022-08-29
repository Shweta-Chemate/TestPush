package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Company {

  private String partnerName;
  private String partnerId;
  private String puid;
  private String domainIdentifier;
  private boolean demoAccount;
  private boolean hcaas;
  private List<UserRole> roleList;

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
