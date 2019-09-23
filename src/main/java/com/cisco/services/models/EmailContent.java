package com.cisco.services.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Email content structure")
public class EmailContent {
	@ApiModelProperty(notes = "Email body subject")
	private String subject;

	@ApiModelProperty(notes = "The Email body. Supports thymeleaf template content (set values in templateValues). Supports HTML content (set htmlBody to true)",
			example = "<b>Email body example. My name is <span th:text='${pName}'></span></b>")
	private String body;

	@ApiModelProperty(notes = "A flag to indicate if the body should be treated as HTML text")
	private Boolean htmlBody = false;

	public EmailContent() {}

	public EmailContent(EmailContent content) {
		this.subject = content.getSubject();
		this.body = content.getBody();
		this.htmlBody = content.isHtmlBody();
	}

	public EmailContent(String subject, String body, Boolean htmlBody) {
		this.subject = subject;
		this.body = body;
		this.htmlBody = htmlBody;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public Boolean isHtmlBody() { return htmlBody; }

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
