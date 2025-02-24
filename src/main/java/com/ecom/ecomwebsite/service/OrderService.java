package com.ecom.ecomwebsite.service;

import com.ecom.ecomwebsite.model.Order;
import com.ecom.ecomwebsite.model.User;
import com.ecom.ecomwebsite.repository.OrderRepository;
import com.ecom.ecomwebsite.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // ✅ Get Order History for a Customer
    public ResponseEntity<?> getOrderHistory(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        List<Order> orders = orderRepository.findByUser(user.get());

        if (orders.isEmpty()) {
            return ResponseEntity.ok("No orders found for this user.");
        }

        return ResponseEntity.ok(orders);
    }

    // ✅ Track Order Status
    public ResponseEntity<String> trackOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found.");
        }
        return ResponseEntity.ok("Order ID: " + orderId + " is currently: " + order.get().getStatus());
    }
}
