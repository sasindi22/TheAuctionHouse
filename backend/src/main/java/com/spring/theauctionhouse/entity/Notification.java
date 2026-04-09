package com.spring.theauctionhouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String title;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

}
