package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonAlias;

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

	public void setStreet(String street) {
		this.street = street;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	private String street;
	private String city;
	private String state;
	private String zipcode;

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

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

	@JsonAlias({ "userId" })
	public String getCecId() {
		return cecId;
	}

	public void setCecId(String cecId) {
		this.cecId = cecId;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@JsonAlias({ "emailId" })
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@JsonAlias({ "telephone" })
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@JsonAlias({ "userTitle" })
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
