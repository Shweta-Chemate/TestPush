package com.cisco.services.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Email request structure")
public class EmailRequest extends EmailContent {
	@ApiModelProperty(notes = "A single 'From' address")
	private String from;

	@NotBlank(message = "'To' address is required")
	@ApiModelProperty(notes = "'To' addresses in RFC822 format", required = true)
	private String to;

	@ApiModelProperty(notes = "'CC' addresses in RFC822 format")
	private String cc;

	@ApiModelProperty(notes = "'ReplyTo' address in RFC822 format")
	private String replyTo;

	@ApiModelProperty(notes = "A unique identifier for the caller")
	private String source;

	@ApiModelProperty(notes = "The template key/value map for the thymeleaf attributes in the email body", example = "{\"pName\": \"James\"}")
	private Map<String, Object> templateValues;

	public EmailRequest () {}

	public EmailRequest(String to, String from, String cc, String replyTo, String source, String subject, String body, Boolean htmlBody, Map<String, Object> templateValues) {
		super(subject, body, htmlBody);
		this.to = to;
		this.from = from;
		this.cc = cc;
		this.replyTo = replyTo;
		this.source = source;
		this.templateValues = templateValues;
	}

	public EmailRequest(EmailRequest request) {
		this(request.getTo(), request.getFrom(), request.getCc(), request.getReplyTo(), request.getSource(), request.getSubject(), request.getBody(), request.isHtmlBody(), request.getTemplateValues());
	}

	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public String getCc() {	return cc; }

	public String getReplyTo() {
		return replyTo;
	}

	public String getSource() { return source; }

	public void setSource(String source) { this.source = source; }

	public Map<String, Object> getTemplateValues() { return templateValues; }
}
