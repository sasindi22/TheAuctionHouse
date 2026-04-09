package com.spring.theauctionhouse.service;

import com.spring.theauctionhouse.entity.NotificationType;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final NotificationService notificationService;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (!verificationService.isVerified(user.getEmail())) {
            throw new RuntimeException("Email not verified");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));
        verificationService.sendOtp(email);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        boolean isVerified = verificationService.verifyOtp(email, otp);
        if (!isVerified) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getProfile(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(String email, User details) {
        User user = getProfile(email);
        user.setName(details.getName());
        user.setBio(details.getBio());
        user.setDob(details.getDob());
        user.setMobile(details.getMobile());
        user.setAddress(details.getAddress());
        if (details.getProfileImage() != null) {
            user.setProfileImage(details.getProfileImage());
        }
        return userRepository.save(user);
    }

    public void applyStrike(User user) {
        user.setStrikes(user.getStrikes() + 1);

        String strikeTitle = "Account Strike: Level " + user.getStrikes();
        String strikeMsg = "You received a strike for failing to complete a payment. If you keep " +
                "ignoring won bid items for three times row, your account will get banned!";

        if (user.getStrikes() >= 3) {
            user.setBanned(true);
            strikeMsg += " You have reached 3 strikes and your account is now banned.";
        }

        notificationService.createNotification(user, strikeTitle, strikeMsg, NotificationType.STRIKE, true);
        userRepository.save(user);
    }

}

