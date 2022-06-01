package com.cisco.cx.training.models;

import java.io.Serializable;

public class SuccessTipsAttachment implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5038905336450032758L;

	private String url;
	
	private String urlDescription;
	
	private String urlTitle;
	
	private String attachmentType;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlDescription() {
		return urlDescription;
	}

	public void setUrlDescription(String urlDescription) {
		this.urlDescription = urlDescription;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getUrlTitle() {
		return urlTitle;
	}

	public void setUrlTitle(String urlTitle) {
		this.urlTitle = urlTitle;
	}

}
