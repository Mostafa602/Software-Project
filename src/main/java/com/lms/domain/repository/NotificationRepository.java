package com.lms.domain.repository;

import com.lms.domain.model.notification.Notification;
import com.lms.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndRead(User user, Boolean isRead);
    List<Notification> findByUser( User user);
}