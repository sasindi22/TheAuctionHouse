package com.spring.theauctionhouse.service;

import com.spring.theauctionhouse.entity.Notification;
import com.spring.theauctionhouse.entity.NotificationType;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUserOrderByTimestampDesc(user);
    }

    public void createNotification(User user, String title, String message, NotificationType type, boolean sendEmail) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setType(type);
        notificationRepository.save(notification);

        if (sendEmail) {
            emailService.sendEmail(user.getEmail(), title, message);
        }
    }

    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsReadForUser(User user) {
        List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
