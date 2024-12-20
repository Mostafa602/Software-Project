package com.lms.domain.service;

import com.lms.domain.dto.notification.NotificationDto;
import com.lms.domain.model.notification.Notification;
import com.lms.domain.model.user.User;
import com.lms.domain.repository.NotificationRepository;
import com.lms.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNotification() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        notificationService.addNotification("New Notification", userId, "INFO");

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getContent()).isEqualTo("New Notification");
        assertThat(savedNotification.getType()).isEqualTo("INFO");
        assertThat(savedNotification.getUser()).isEqualTo(user);
        assertThat(savedNotification.getRead()).isFalse();
    }

    @Test
    public void testAddNotificationUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.addNotification("New Notification", userId, "INFO"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Course not found with ID: " + userId);
    }

    @Test
    public void testGetNotifications() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Notification notification1 = new Notification("Content 1", false, user, "INFO");
        notification1.setId(1L);
        Notification notification2 = new Notification("Content 2", true, user, "ALERT");
        notification2.setId(2L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification1, notification2));

        List<NotificationDto> notifications = notificationService.getNotifications(userId, null);

        assertThat(notifications).hasSize(2);
        assertThat(notifications.get(0).getContent()).isEqualTo("Content 1");
        assertThat(notifications.get(1).getContent()).isEqualTo("Content 2");

        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    public void testGetNotificationsUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotifications(userId, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}