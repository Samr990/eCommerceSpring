package com.ecom.ecomwebsite.controller;

import com.ecom.ecomwebsite.dto.UserRegistrationRequest;
import com.ecom.ecomwebsite.dto.ResetPasswordRequest;
import com.ecom.ecomwebsite.dto.UserLoginRequest;
import com.ecom.ecomwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    // 🔹 Register a New User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        return userService.registerUser(request);
    }

    // 🔹 Login User
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

    // 🔹 Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return userService.resetPassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getNewPassword());
    }

    // 🔹 Get All Users (Admin Only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return userService.getAllUsers();
    }
}
