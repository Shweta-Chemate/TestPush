package com.cisco.cx.training.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsWithCompanyList {
	
	private UserProfile ciscoUserProfileSchema;
	private List<Company> companyList;
	
	public UserProfile getCiscoUserProfileSchema() {
		return ciscoUserProfileSchema;
	}
	public void setCiscoUserProfileSchema(UserProfile ciscoUserProfileSchema) {
		this.ciscoUserProfileSchema = ciscoUserProfileSchema;
	}
	public List<Company> getCompanyList() {
		return companyList;  //NOSONAR
	}
	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;  //NOSONAR
	}
	@Override
	public String toString() {
		return "UserDetailsWithCompanyList [ciscoUserProfileSchema=" + ciscoUserProfileSchema + ", companyList="
				+ companyList + "]";
	}
}
