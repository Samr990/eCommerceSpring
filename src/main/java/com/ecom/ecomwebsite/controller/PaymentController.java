package com.ecom.ecomwebsite.controller;

import com.ecom.ecomwebsite.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ Process Payment for an Order
    @PostMapping("/pay")
    public ResponseEntity<String> processPayment(
             @RequestParam Long orderId,
            @RequestParam String paymentMethod) {
        return paymentService.processPayment(orderId, paymentMethod);
    }
}
