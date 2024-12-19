
package com.lms.controller;

import com.lms.domain.model.notification.Notification;
import com.lms.domain.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userid}/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getNotifications(
            @PathVariable Long userId,
            @RequestParam(required = false) Boolean read) {
        return notificationService.getNotifications(userId, read);
    }

}

