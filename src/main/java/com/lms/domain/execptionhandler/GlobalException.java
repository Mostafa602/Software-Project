package com.lms.domain.execptionhandler;

import com.lms.domain.dto.BasicResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new BasicResponseDto("error", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> unauthorizedExceptionHandler(UnauthorizedAccessException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new BasicResponseDto("error", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> missingFieldsExceptionHandler(MissingFieldsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BasicResponseDto("error", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> conflictExceptionHandler(ConflictException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new BasicResponseDto("error", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> internalServerExceptionHandler(InternalServerException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BasicResponseDto("error", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BasicResponseDto("error", "wrong format"));
    }

    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> methodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new BasicResponseDto("error", e.getMessage()));
    }

    // should not appear
    @ExceptionHandler
    public ResponseEntity<BasicResponseDto> genericExceptionHandler(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BasicResponseDto("error", e.getMessage() + e.getClass().getName()));
    }


    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Map<String, Object>> handleEmailSendingException(EmailSendingException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Email Sending Error");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }





}
