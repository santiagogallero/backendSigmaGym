package com.sigma.gym.exceptions;

public class InvoicingException extends RuntimeException {
    
    private final String errorCode;
    
    public InvoicingException(String message) {
        super(message);
        this.errorCode = "INVOICING_ERROR";
    }
    
    public InvoicingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public InvoicingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INVOICING_ERROR";
    }
    
    public InvoicingException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // Specific exception types
    public static class ProfileInvalidException extends InvoicingException {
        public ProfileInvalidException(String message) {
            super(message, "PROFILE_INVALID");
        }
    }
    
    public static class AlreadyInvoicedException extends InvoicingException {
        public AlreadyInvoicedException(String message) {
            super(message, "ALREADY_INVOICED");
        }
    }
    
    public static class SequenceErrorException extends InvoicingException {
        public SequenceErrorException(String message) {
            super(message, "SEQUENCE_ERROR");
        }
        
        public SequenceErrorException(String message, Throwable cause) {
            super(message, "SEQUENCE_ERROR", cause);
        }
    }
    
    public static class PdfErrorException extends InvoicingException {
        public PdfErrorException(String message) {
            super(message, "PDF_ERROR");
        }
        
        public PdfErrorException(String message, Throwable cause) {
            super(message, "PDF_ERROR", cause);
        }
    }
}
