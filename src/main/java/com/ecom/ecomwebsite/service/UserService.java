package com.ecom.ecomwebsite.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;
import com.ecom.ecomwebsite.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    //  Register a new user with role assignment
    public ResponseEntity<String> registerUser(User user, RoleType role) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already registered.");
        }

        // Assign role (default to CUSTOMER if not specified)
        user.setRole(role != null ? role : RoleType.CUSTOMER);

        // Save user to DB
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully with role: " + user.getRole());
    }

    // User Login Based on Role
    public ResponseEntity<String> loginUser(String userName, String password) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User not found.");
        }

        User user = userOptional.get();

        // Check password
        if (!user.getPasswd().equals(password)) {
            return ResponseEntity.badRequest().body("Error: Invalid username or password.");
        }

        // Return response based on role
        switch (user.getRole()) {
            case ADMIN:
                return ResponseEntity.ok("Login successful. Welcome, Admin " + user.getUserName() + "!");
            case SELLER:
                return ResponseEntity.ok("Login successful. Welcome, Seller " + user.getUserName() + "!");
            case CUSTOMER:
                return ResponseEntity.ok("Login successful. Welcome, " + user.getUserName() + "!");
            default:
                return ResponseEntity.badRequest().body("Error: Role not recognized.");
        }
    }

    // Reset Password
    public ResponseEntity<String> resetPassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User with email " + email + " not found.");
        }

        User user = userOptional.get();
        user.setPasswd(newPassword); //  Fixed password field issue
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful.");
    }
    
    // allow admin to view list of users
    public ResponseEntity<?> getAllUsers(String adminEmail) {
        Optional<User> adminUser = userRepository.findByEmail(adminEmail);
        
        // Check if the requester exists and is an ADMIN
        if (adminUser.isEmpty() || adminUser.get().getRole() != RoleType.ADMIN) {
            return ResponseEntity.status(403).body("Access Denied: Only Admins can view users.");
        }

        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
