package com.cisco.cx.training.app.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String e) {
        super(e);
    }

    public NotFoundException(String e, Throwable cause) {
        super(e, cause);
    }
}
