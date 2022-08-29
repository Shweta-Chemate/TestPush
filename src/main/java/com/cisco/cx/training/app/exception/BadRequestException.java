package com.cisco.cx.training.app.exception;

@SuppressWarnings("serial")
public class BadRequestException extends RuntimeException {
  public BadRequestException(String e) {
    super(e);
  }
}
