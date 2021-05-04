package com.cisco.cx.training.models;

public class PIWSession {

	private String sessionId;
	private Long sessionStartDate;
	private String presenterName;
	private String registrationUrl;
	private Boolean scheduled;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getSessionStartDate() {
		return sessionStartDate;
	}

	public void setSessionStartDate(Long sessionStartDate) {
		this.sessionStartDate = sessionStartDate;
	}

	public String getPresenterName() {
		return presenterName;
	}

	public void setPresenterName(String presenterName) {
		this.presenterName = presenterName;
	}

	public String getRegistrationUrl() {
		return registrationUrl;
	}

	public void setRegistrationUrl(String registrationUrl) {
		this.registrationUrl = registrationUrl;
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
	}

}
