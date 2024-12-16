package com.lms.domain.execptionhandler;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    public UnauthorizedAccessException() {
        super("unauthorized access");
    }
}