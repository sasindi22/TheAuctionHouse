package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.config.JwtService;
import com.spring.theauctionhouse.dto.AuthResponse;
import com.spring.theauctionhouse.dto.LoginRequest;
import com.spring.theauctionhouse.dto.RegisterResponse;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.service.UserService;
import com.spring.theauctionhouse.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationService verificationService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        verificationService.sendOtp(email);
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        boolean verified = verificationService.verifyOtp(email, otp);

        if (verified) {
            return ResponseEntity.ok("Email verified");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String email) {
        try {
            verificationService.sendOtp(email);
            return ResponseEntity.ok("OTP resent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(
                    new RegisterResponse(user.getEmail(), "Registration successful. OTP sent to your email")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new RegisterResponse(user.getEmail(), "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            String token = jwtService.generateToken(request.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            userService.forgotPassword(email); // generate OTP + send email
            return ResponseEntity.ok("Password reset OTP sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Email not found");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword
    ) {
        try {
            userService.resetPassword(email, otp, newPassword);
            return ResponseEntity.ok("Password reset successful. You can now login.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }

}

