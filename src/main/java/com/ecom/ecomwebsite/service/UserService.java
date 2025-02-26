package com.ecom.ecomwebsite.service;

import com.ecom.ecomwebsite.dto.UserRegistrationRequest;
import com.ecom.ecomwebsite.dto.UserLoginRequest;
import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;
import com.ecom.ecomwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 🔹 Register a New User
    public ResponseEntity<String> registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already registered.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswd(passwordEncoder.encode(request.getPassword())); // ✅ Encrypt Password
        user.setUserName(request.getUserName());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole() != null ? request.getRole() : RoleType.CUSTOMER); // Default Role = CUSTOMER

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully with role: " + user.getRole());
    }

    // 🔹 Authenticate User (Login)
    public ResponseEntity<?> loginUser(UserLoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Error: Invalid email or password.");
        }

        User user = userOptional.get();

        // ✅ Verify the encoded password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Error: Invalid email or password.");
        }

        // ✅ Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok("Login successful. Welcome, " + user.getUsername() + " (" + user.getRole() + ")!");
    }

    // 🔹 Get All Users (Admin Only)
    public ResponseEntity<?> getAllUsers() {
        User adminUser = getAuthenticatedUser();

        if (adminUser.getRole() != RoleType.ADMIN) {
            return ResponseEntity.status(403).body("Access Denied: Only Admins can view users.");
        }

        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // 🔹 Reset Password
    public ResponseEntity<String> resetPassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User with email " + email + " not found.");
        }

        User user = userOptional.get();
        user.setPasswd(passwordEncoder.encode(newPassword)); // ✅ Encrypt Password
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful.");
    }

    // ✅ Get the currently authenticated user
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
