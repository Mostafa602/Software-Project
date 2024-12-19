
package com.lms.domain.repository;

import com.lms.domain.model.notification.Notification;
import com.lms.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsRead(User user, Boolean isRead);
    List<Notification> findByUser( User user);
}

