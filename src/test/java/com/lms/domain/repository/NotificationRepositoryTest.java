package com.lms.domain.repository;

import com.lms.domain.model.notification.Notification;
import com.lms.domain.model.user.User;
import com.lms.domain.model.user.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("Ahmed", "Hassan", "ahmed.hassan@example.com", "securePassword", Roles.ROLE_STUDENT);
        userRepository.save(user);

        Notification notification1 = new Notification("Notification 1", false, user, "GENERAL");
        Notification notification2 = new Notification("Notification 2", true, user, "REMINDER");
        Notification notification3 = new Notification("Notification 3", false, user, "ALERT");

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
    }

    @Test
    public void testFindByUserAndIsRead() {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsRead(user, false);
        assertThat(unreadNotifications).hasSize(2);
        assertThat(unreadNotifications).extracting(Notification::getContent)
                .containsExactlyInAnyOrder("Notification 1", "Notification 3");

        List<Notification> readNotifications = notificationRepository.findByUserAndIsRead(user, true);
        assertThat(readNotifications).hasSize(1);
        assertThat(readNotifications.get(0).getContent()).isEqualTo("Notification 2");
    }

    @Test
    public void testFindByUser() {
        List<Notification> notifications = notificationRepository.findByUser(user);
        assertThat(notifications).hasSize(3);
        assertThat(notifications).extracting(Notification::getContent)
                .containsExactlyInAnyOrder("Notification 1", "Notification 2", "Notification 3");
    }
}