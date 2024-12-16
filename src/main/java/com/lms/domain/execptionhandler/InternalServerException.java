package com.lms.domain.execptionhandler;

public class InternalServerException extends RuntimeException {
    public InternalServerException() {
        super("Internal Server Error");
    }
}
