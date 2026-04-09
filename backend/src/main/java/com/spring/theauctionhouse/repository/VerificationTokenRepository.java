package com.spring.theauctionhouse.repository;

import com.spring.theauctionhouse.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByEmail(String email);

}
