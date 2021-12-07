package com.cisco.cx.training.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Company {
	
	private String partnerName;
	private String partnerId;
	private String puid;
	private String domainIdentifier;
	private boolean demoAccount;
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
		return roleList; //NOSONAR
	}
	public void setRoleList(List<UserRole> roleList) {
		this.roleList = roleList; //NOSONAR
	}
	public boolean isDemoAccount() {
		return demoAccount;
	}
	public void setDemoAccount(boolean demoAccount) {
		this.demoAccount = demoAccount;
	}
     
}
