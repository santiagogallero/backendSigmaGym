package com.sigma.gym.exceptions;

public class WaitlistException extends RuntimeException {
    private final String errorCode;

    public WaitlistException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public WaitlistException(String message) {
        super(message);
        this.errorCode = "WAITLIST_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
