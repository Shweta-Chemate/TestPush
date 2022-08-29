package com.cisco.cx.training.app.service;

import com.cisco.cx.training.models.UserDetails;
import com.cisco.cx.training.models.UserDetailsWithCompanyList;

public interface PartnerProfileService {

  UserDetails fetchUserDetails(String xMasheryHandshake);

  UserDetailsWithCompanyList fetchUserDetailsWithCompanyList(String xMasheryHandshake);

  void setEntitlementUrl(String entitlementUrl);

  String getEntitlementUrl();

  boolean isPLSActive(String xMasheryHandshake, String partnerId) throws Exception; // NOSONAR

  public boolean getHcaasStatusForPartner(String xMasheryHandshake);
}
