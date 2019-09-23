package com.cisco.services.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericException extends RuntimeException{
    public GenericException(String e) {
        super(e);
    }

    public GenericException(String e, Throwable cause) {
        super(e, cause);
    }
}
