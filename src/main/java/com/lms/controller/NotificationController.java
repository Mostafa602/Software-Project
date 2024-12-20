package com.lms.controller;

import com.lms.domain.model.notification.Notification;
import com.lms.domain.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userid}/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Object> getNotifications(
            @PathVariable Long userid,
            @RequestParam(required = false) Boolean read) {
        try {
            return ResponseEntity.ok(notificationService.getNotifications(userid, read));
        }
        catch (IllegalArgumentException ex) {

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
    }
}