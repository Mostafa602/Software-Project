package com.lms.domain.service;

import com.lms.domain.dto.notification.NotificationDto;
import com.lms.domain.model.notification.Notification;
import com.lms.domain.model.user.User;
import com.lms.domain.repository.NotificationRepository;
import com.lms.domain.repository.UserRepository;
import com.lms.domain.execptionhandler.ConflictException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import java.util.Optional;
import java.util.List;


@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void addNotification( String content, Long userId, String type    ){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + userId));
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setType(type);
        notification.setRead(false);
        notification.setUser(user);
        notificationRepository.save(notification);
    }

    private void makeNotificationRead(List<Notification> notifications  ){
        notifications.forEach( notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public List<NotificationDto> getNotifications(Long userId, Boolean isRead) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications;

        if (isRead == null) {
            notifications = notificationRepository.findByUser(user);
            List<NotificationDto> answer = notifications.stream()
                    .map(notification -> new NotificationDto(notification.getId(), notification.getContent(), notification.getRead(), notification.getType())
                    )
                    .toList();
            makeNotificationRead(notifications);
            return answer;
        } else {
            notifications = notificationRepository.findByUserAndIsRead(user, isRead);
            List<NotificationDto> answer = notifications.stream()
                    .map(notification -> new NotificationDto(notification.getId(), notification.getContent(), notification.getRead(), notification.getType())
                    )
                    .toList();
            if ( !isRead ){
                makeNotificationRead(notifications);
            }
            return answer;
        }
    }

}