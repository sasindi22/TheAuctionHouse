package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.Notification;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.service.NotificationService;
import com.spring.theauctionhouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getNotificationsByUser(user));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        User user = userService.getCurrentUser();
        notificationService.markAllAsReadForUser(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
