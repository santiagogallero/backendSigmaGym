package com.sigma.gym.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    
    private int statusCode = 400; // Bad Request por defecto
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
