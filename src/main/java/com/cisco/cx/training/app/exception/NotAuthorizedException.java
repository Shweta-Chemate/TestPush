package com.cisco.cx.training.app.exception;

@SuppressWarnings("serial")
public class NotAuthorizedException extends RuntimeException{
    public NotAuthorizedException(String e) {
        super(e);
    }

    public NotAuthorizedException(String e, Throwable cause) {
        super(e, cause);
    }
}
