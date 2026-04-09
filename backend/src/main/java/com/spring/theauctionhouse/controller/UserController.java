package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateProfile(@RequestBody User user, Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), user));
    }

}
