package com.sigma.gym.exceptions;

public class ClassSessionNotFoundException extends RuntimeException {
    public ClassSessionNotFoundException(String message) {
        super(message);
    }
    
    public ClassSessionNotFoundException(Long classSessionId) {
        super("Class session not found with ID: " + classSessionId);
    }
}
