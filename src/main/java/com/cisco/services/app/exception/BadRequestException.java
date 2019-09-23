package com.cisco.services.app.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String e) {
        super(e);
    }
}