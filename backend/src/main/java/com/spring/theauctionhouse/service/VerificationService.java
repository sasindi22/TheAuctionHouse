package com.spring.theauctionhouse.service;

import com.spring.theauctionhouse.entity.VerificationToken;
import com.spring.theauctionhouse.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationTokenRepository repo;
    private final EmailService emailService;

    public void sendOtp(String email) {
        String otp = emailService.generateOtp();

        VerificationToken token = repo.findByEmail(email).orElse(new VerificationToken());

        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiry(LocalDateTime.now().plusMinutes(5));
        token.setVerified(false);

        repo.save(token);

        emailService.sendEmail(email, "Verify Email", "Your OTP: " + otp);
    }

    public boolean verifyOtp(String email, String otp) {
        VerificationToken token = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No OTP found"));

        if (token.getOtp() != null &&
                token.getOtp().equals(otp) &&
                token.getExpiry().isAfter(LocalDateTime.now())) {

            token.setVerified(true);
            repo.save(token);
            return true;
        }

        return false;
    }

    public boolean isVerified(String email) {
        return repo.findByEmail(email)
                .map(VerificationToken::isVerified)
                .orElse(false);
    }
}
