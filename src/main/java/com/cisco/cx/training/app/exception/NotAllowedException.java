package com.cisco.cx.training.app.exception;

public class NotAllowedException extends RuntimeException{
    public NotAllowedException(String e) {
        super(e);
    }
}