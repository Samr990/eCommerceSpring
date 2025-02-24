package com.ecom.ecomwebsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecom.ecomwebsite.dto.ResetPasswordRequest;
import com.ecom.ecomwebsite.dto.UserLoginRequest;
import com.ecom.ecomwebsite.dto.UserRegistrationRequest;
import com.ecom.ecomwebsite.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    //  Register a User with Role Assignment
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        return userService.registerUser(request.getUser(), request.getRole());
    }

    //  User Login (Returns role-based response)
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginRequest loginRequest) {
        return userService.loginUser(loginRequest.getUserName(), loginRequest.getPassword());
    }

    //  Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return userService.resetPassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getNewPassword());
    }
    
    //admin views all user
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestParam String adminEmail) {
        return userService.getAllUsers(adminEmail);
    }
}
