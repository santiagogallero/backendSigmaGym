package com.sigma.gym.exceptions;

public class BookingPolicyViolationException extends RuntimeException {
    private final String errorCode;
    
    public BookingPolicyViolationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BookingPolicyViolationException(String message) {
        super(message);
        this.errorCode = "POLICY_VIOLATION";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
