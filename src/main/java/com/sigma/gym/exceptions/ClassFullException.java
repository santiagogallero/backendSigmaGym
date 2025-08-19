package com.sigma.gym.exceptions;

public class ClassFullException extends RuntimeException {
    public ClassFullException(String message) {
        super(message);
    }
    
    public ClassFullException(Long classSessionId) {
        super("Class session is full: " + classSessionId);
    }
}
