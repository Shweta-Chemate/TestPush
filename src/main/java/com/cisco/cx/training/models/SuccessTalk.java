package com.cisco.cx.training.models;

import java.util.List;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModelProperty;

public class SuccessTalk implements HasId, Comparable<SuccessTalk> {

	@ApiModelProperty(notes = "Unique Id of the SuccessTalk", example = "STK-01")
	private String successTalkId;
	@ApiModelProperty(notes = "Quarter", example = "Q120")
	private String quarter;
	@ApiModelProperty(notes = "Title of the SuccessTalk", example = "Cisco DNA Center Getting Started")
	private String title;
	@ApiModelProperty(notes = "Description of the SuccessTalk", example = "We cover subjects including interface and network design overview, policy \n"
			+ "management and deployment, device provisioning, and automation/assurance.")
	private String description;
	@ApiModelProperty(notes = "URL of the image to be displayed")
	private String imageUrl;
	@ApiModelProperty(notes = "Status of the SuccessTalk", example = "Recommended")
	private String status;
	@ApiModelProperty(notes = "registration updated timestamp", example = "Recommended")
	private String regTimestamp;
	@ApiModelProperty(notes = "Pitstop", example = "Onboard")
	private String recordingUrl;
	@ApiModelProperty(notes = "Duration of the session in seconds", example = "3600")
	private String duration;
	@ApiModelProperty(notes = "Is bookmark", example = "true")
	private boolean bookmark;
	@ApiModelProperty(notes = "Boolean to identify if this is a technical session")
	private boolean technicalSession;
	@ApiModelProperty(notes = "List of available sessions for selected SuccessTalk")
	private List<SuccessTalkSession> sessions;

	public enum SuccessTalkStatusEnum {
        ATTENDED("Attended"),

        RECOMMENDED("Recommended"),

        REQUESTED("Requested"),

        REGISTERED("Registered"),
        
        CONFIRMED("Confirmed"),
        
        PROPOSED("Proposed"),
        
        CANCELLED("Cancelled");

        private String value;

        SuccessTalkStatusEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SuccessTalk.SuccessTalkStatusEnum fromValue(String text) {
            for (SuccessTalk.SuccessTalkStatusEnum b : SuccessTalk.SuccessTalkStatusEnum.values()) {
                if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
	}
	
	

	public String getSuccessTalkId() {
		return successTalkId;
	}

	public void setSuccessTalkId(String successTalkId) {
		this.successTalkId = successTalkId;
	}

	@Override
	public String getDocId() {
		return successTalkId;
	}

	@Override
	public void setDocId(String id) {
		this.successTalkId = id;

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public boolean isBookmark() {
		return bookmark;
	}

	public void setBookmark(boolean bookmark) {
		this.bookmark = bookmark;
	}

	public List<SuccessTalkSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<SuccessTalkSession> sessions) {
		this.sessions = sessions;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getRecordingUrl() {
		return recordingUrl;
	}

	public void setRecordingUrl(String recordingUrl) {
		this.recordingUrl = recordingUrl;
	}

	@Override
	public int compareTo(SuccessTalk successTalk) {
		return this.getSuccessTalkId().compareTo(((SuccessTalk) successTalk).getSuccessTalkId());
	}

	public Boolean getTechnicalSession() {
		return technicalSession;
	}

	public void setTechnicalSession(Boolean technicalSession) {
		this.technicalSession = technicalSession;
	}

	public String getRegTimestamp() {
		return regTimestamp;
	}

	public void setRegTimestamp(String regTimestamp) {
		this.regTimestamp = regTimestamp;
	}

}
