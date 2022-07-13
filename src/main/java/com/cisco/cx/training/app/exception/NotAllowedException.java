package com.cisco.cx.training.app.exception;

@SuppressWarnings("serial")
public class NotAllowedException extends RuntimeException {
  public NotAllowedException(String e) {
    super(e);
  }
}
