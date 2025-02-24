package com.ecom.ecomwebsite.controller;

import com.ecom.ecomwebsite.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ Get Order History 
    @GetMapping("/history/{userEmail}")
    public ResponseEntity<?> getOrderHistory(@PathVariable String userEmail) {
        return orderService.getOrderHistory(userEmail);
    }

    // ✅ Track Order Status
    @GetMapping("/track/{orderId}")
    public ResponseEntity<String> trackOrder(@PathVariable Long orderId) {
        return orderService.trackOrder(orderId);
    }
}
