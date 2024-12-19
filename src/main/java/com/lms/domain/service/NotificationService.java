package com.lms.domain.service;

import com.lms.domain.model.notification.Notification;
import com.lms.domain.model.user.User;
import com.lms.domain.repository.NotificationRepository;
import com.lms.domain.repository.UserRepository;
import com.lms.domain.execptionhandler.ConflictException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Optional;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void addNotification(String content, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + userId));
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setUser(user);
        notificationRepository.save(notification);
    }

    private void makeNotificationRead(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getNotifications(Long userId, Boolean isRead) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (isRead == null) {
            return notificationRepository.findByUser(user);
        } else if (isRead) {
            return notificationRepository.findByUserAndRead(user, isRead);
        } else {
            return notificationRepository.findByUserAndRead(user, isRead);
        }
    }

}