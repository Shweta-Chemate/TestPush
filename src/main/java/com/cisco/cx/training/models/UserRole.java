package com.cisco.cx.training.models;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRole {
	
	private String roleName; 
	private String roleDescription; 
	private String roleAbv;
	private int roleId;
	private List<String> resourceList;
//	private List<CustomerInfo> customerList;
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	public String getRoleAbv() {
		return roleAbv;
	}
	public void setRoleAbv(String roleAbv) {
		this.roleAbv = roleAbv;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public List<String> getResourceList() {
		return resourceList;
	}
	public void setResourceList(List<String> resourceList) {
		this.resourceList = resourceList;
	}
//	public List<CustomerInfo> getCustomerList() {
//		return customerList;
//	}
//	public void setCustomerList(List<CustomerInfo> customerList) {
//		this.customerList = customerList;
//	}
	

}
