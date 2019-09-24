package com.cisco.cx.training.app.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String e) {
        super(e);
    }
}