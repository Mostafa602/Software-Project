package com.lms.domain.dto;

public class BasicResponseDto {
    String status;
    String message;

    public BasicResponseDto(String status, String message) {
        this.message = message;
        this.status = status;
    }

    public BasicResponseDto() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
