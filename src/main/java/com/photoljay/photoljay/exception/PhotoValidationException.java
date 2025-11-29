package com.photoljay.photoljay.exception;


public class PhotoValidationException extends RuntimeException {
    public PhotoValidationException(String message) {
        super(message);
    }
    
    public PhotoValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}