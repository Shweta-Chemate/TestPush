package com.cisco.cx.training.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class UserProfile {
	private static final Logger LOG = LoggerFactory.getLogger(UserProfile.class.getName());

	private String userId;
	private String ciscoUid;
	private String mailId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String phoneNo;
	private String accessLevel;
	private String country;
	private String userType;

	private boolean newUser;

	/**
	 * The getter method for firstName
	 *
	 * @return firstName.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * The setter method for firstName
	 *
	 * @param firstName
	 *            .
	 **/
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * The getter method for lastName
	 *
	 * @return lastName.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * The setter method for lastName
	 *
	 * @param lastName
	 *            .
	 **/
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * The getter method for middleName
	 *
	 * @return middleName.
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * The setter method for middleName
	 *
	 * @param middleName
	 *            .
	 **/
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * The getter method for userId
	 *
	 * @return userId.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * The setter method for userId
	 *
	 * @param userId
	 *            .
	 **/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * The getter method for ciscoUid
	 *
	 * @return ciscoUid.
	 */
	public String getCiscoUid() {
		return ciscoUid;
	}

	/**
	 * The setter method for ciscoUid
	 *
	 * @param ciscoUid
	 *            .
	 **/
	public void setCiscoUid(String ciscoUid) {
		this.ciscoUid = ciscoUid;
	}

	/**
	 * The getter method for mail
	 *
	 * @return mail.
	 */
	public String getMailId() {
		return mailId;
	}

	/**
	 * The setter method for mail
	 *
	 * @param mailId
	 *            .
	 **/
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	/**
	 * The getter method for phoneNo
	 *
	 * @return phoneNo.
	 */
	public String getPhoneNo() {
		return phoneNo;
	}

	/**
	 * The setter method for phoneNo
	 *
	 * @param phoneNo
	 *            .
	 **/
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	/**
	 * The getter method for accessLevel
	 *
	 * @return accessLevel.
	 **/
	public String getAccessLevel() {
		return accessLevel;
	}

	/**
	 * The setter method for accessLevel
	 *
	 * @param accessLevel
	 *            .
	 **/
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		Field[] declaredFields = this.getClass().getDeclaredFields();

		for (Field tempField : declaredFields) {
			String fieldName = tempField.getName();
			Object fieldValue = null;
			try {
				fieldValue = tempField.get(this);
			} catch (IllegalArgumentException e) {
				LOG.debug("UserProfile.toString() - IllegalArgumentException");
			} catch (IllegalAccessException e) {
				LOG.debug("UserProfile.toString() - IllegalAccessException");
			}

			builder.append(fieldName + "=" + fieldValue + "; ");
		}
		return builder.toString();
	}

}
