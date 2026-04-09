package com.spring.theauctionhouse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    private String bio;
    private LocalDate dob;
    private String address;
    private String mobile;

    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    private LocalDateTime createdAt = LocalDateTime.now();

    private int strikes = 0;
    private boolean isBanned = false;

    private boolean enabled = false;
}

