package com.cisco.services.app.exception;

public class ErrorResponse {

	private int status;
	private String message;
	private Reason reason;

	public ErrorResponse(int status, String message, String errorCode, String errorInfo) {
		this.status = status;
		this.message = message;
		this.reason = new Reason(errorCode, errorInfo);
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Reason getReason() {
		return reason;
	}
	public void setReason(Reason reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "ErrorResponse{" + "status=" + status + ", message='" + message + '\'' + ", reason=" + reason + "}";
	}
}
