package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;


public class SuccessTalkSession implements HasId {

	private String sessionId;
	private Long sessionStartDate;
	private String presenterName;
	private String registrationUrl;
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

	public String getRegistrationUrl() {
		return registrationUrl;
	}

	public void setRegistrationUrl(String registrationUrl) {
		this.registrationUrl = registrationUrl;
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
