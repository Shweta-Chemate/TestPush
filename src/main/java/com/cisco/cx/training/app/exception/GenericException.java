package com.cisco.cx.training.app.exception;


@SuppressWarnings("serial")
public class GenericException extends RuntimeException{
    public GenericException(String e) {
        super(e);
    }

    public GenericException(String e, Throwable cause) {
        super(e, cause);
    }
}
