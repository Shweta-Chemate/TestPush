package com.cisco.cx.training.models;

import java.util.List;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModelProperty;

public class SuccessTalk implements HasId {

	@ApiModelProperty(notes = "Unique Id of the SuccessTalk", example = "STK-01")
	private String successTalkId;
	@ApiModelProperty(notes = "Title of the SuccessTalk", example = "Cisco DNA Center Getting Started")
	private String title;
	@ApiModelProperty(notes = "Description of the SuccessTalk", example = "We cover subjects including interface and network design overview, policy \n"
			+ "management and deployment, device provisioning, and automation/assurance.")
	private String description;
	@ApiModelProperty(notes = "URL of the image to be displayed")
	private String imageURL;
	@ApiModelProperty(notes = "Status of the SuccessTalk", example = "Recommended")
	private SuccessTalkStatusEnum status = SuccessTalkStatusEnum.RECOMMENDED;
	@ApiModelProperty(notes = "Pitstop", example = "Onboard")
	private String recordingURL;
	@ApiModelProperty(notes = "Duration of the session in seconds", example = "3600")
	private Long duration;
	@ApiModelProperty(notes = "Is bookmark", example = "true")
	private boolean bookmark;
	@ApiModelProperty(notes = "List of available sessions for selected SuccessTalk")
	private List<SuccessTalkSession> sessions;

	public enum SuccessTalkStatusEnum {
        COMPLETED("completed"),

        RECOMMENDED("recommended"),

        SCHEDULED("scheduled"),

        REQUESTED("requested"),

        INPROGRESS("in-progress"),

        REGISTERED("registered");

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

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public SuccessTalkStatusEnum getStatus() {
		return status;
	}

	public void setStatus(SuccessTalkStatusEnum status) {
		this.status = status;
	}

	public String getRecordingURL() {
		return recordingURL;
	}

	public void setRecordingURL(String recordingURL) {
		this.recordingURL = recordingURL;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
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

}
