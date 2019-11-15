package com.cisco.cx.training.app.service;

import com.cisco.cx.training.models.UserDetails;

public interface PartnerProfileService {

	UserDetails fetchUserDetails(String xMasheryHandshake);

	void setEntitlementUrl(String entitlementUrl);
	
	String getEntitlementUrl();
	
}