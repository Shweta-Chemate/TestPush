package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SuccessTalkSession implements HasId {

	private String sessionId;
	private Long sessionStartDate;
	private String presenterName;
	private String registrationURL;
	@JsonIgnore
	private String region;
	private Boolean scheduled;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String getDocId() {
		return sessionId;
	}

	@Override
	public void setDocId(String id) {
		this.sessionId = id;

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

	public String getRegistrationURL() {
		return registrationURL;
	}

	public void setRegistrationURL(String registrationURL) {
		this.registrationURL = registrationURL;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
	}

}
