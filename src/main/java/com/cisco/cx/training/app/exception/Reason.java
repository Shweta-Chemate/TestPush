package com.cisco.cx.training.app.exception;

public class Reason {
	private String errorCode;
	private String errorInfo;

	public Reason(String errorCode, String errorInfo) {
		this.errorCode = errorCode;
		this.errorInfo = errorInfo;
	}

	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@Override
	public String toString() {
		return "Reason{" + "errorCode=" + errorCode + ", errorInfo='" + errorInfo + "'}";
	}
}
