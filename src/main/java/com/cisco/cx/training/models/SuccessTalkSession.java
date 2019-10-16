package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;


public class SuccessTalkSession implements HasId {

	private String sessionId;
	private String sessionStartDate;
	private String presenterName;
	private String registrationUrl;
	public RegistrationStatusEnum registrationStatus;
	@JsonIgnore
	private String region;
	private Boolean scheduled;

	public enum RegistrationStatusEnum {
        REGISTERED("registered"),
        
        PENDING("pending"),
        
        CANCELLED("cancelled");

        private String value;

        RegistrationStatusEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SuccessTalkSession.RegistrationStatusEnum fromValue(String text) {
            for (SuccessTalkSession.RegistrationStatusEnum b : SuccessTalkSession.RegistrationStatusEnum.values()) {
                if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
	}
	
	public RegistrationStatusEnum getRegistrationStatus() {
		return registrationStatus;
	}

	public void setRegistrationStatus(RegistrationStatusEnum registrationStatus) {
		this.registrationStatus = registrationStatus;
	}

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

	public String getSessionStartDate() {
		return sessionStartDate;
	}

	public void setSessionStartDate(String sessionStartDate) {
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
