package com.ecom.ecomwebsite.service;

import com.ecom.ecomwebsite.model.Order;
import com.ecom.ecomwebsite.model.Payment;
import com.ecom.ecomwebsite.repository.OrderRepository;
import com.ecom.ecomwebsite.repository.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    // ✅ Process Payment for an Order
    public ResponseEntity<String> processPayment(Long orderId, String paymentMethod) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found.");
        }

        // ✅ Ensure order is in PAYMENT_PENDING state
        if (!order.get().getStatus().equals("PAYMENT_PENDING")) {
            return ResponseEntity.status(400).body("Payment has already been made or order is not pending payment.");
        }

        // ✅ Step 1: Process Payment (Simulated for now)
        Payment payment = new Payment();
        payment.setOrder(order.get());
        payment.setPayMethod(paymentMethod);
        payment.setPayStatus("SUCCESSFUL");
        payment.setPayAmt(order.get().getOrderAmount());
        paymentRepository.save(payment);

        // ✅ Step 2: Update Order Status to CONFIRMED
        Order paidOrder = order.get();
        paidOrder.setStatus("CONFIRMED");
        orderRepository.save(paidOrder);

        // ✅ Step 3: Send Order Confirmation (Simulated as print statement)
        System.out.println("Order Confirmation: Order ID " + orderId + " has been confirmed!");

        return ResponseEntity.ok("Payment successful! Order ID: " + orderId + " is now confirmed.");
    }

}
