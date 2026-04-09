package com.spring.theauctionhouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "verification_token")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String otp;
    private LocalDateTime expiry;

    private boolean verified = false;
}
