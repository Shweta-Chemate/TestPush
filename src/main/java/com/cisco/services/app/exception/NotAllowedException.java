package com.cisco.services.app.exception;

public class NotAllowedException extends RuntimeException{
    public NotAllowedException(String e) {
        super(e);
    }
}