package com.ecom.ecomwebsite.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(updatable = false)
    private Instant orderDate; // Allow null initially

    @Column(nullable = false)
    private String status; // PAYMENT_PENDING, CONFIRMED, SHIPPED, DELIVERED

    private double orderAmount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt; // Allow null initially

    @UpdateTimestamp
    private Instant updatedAt; // Allow null initially

    // ✅ Automatically set timestamps ONLY when order is created
    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = Instant.now(); // Set order date only if null
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }

    // ✅ Only update updatedAt when the order is modified
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // ✅ Constructor without timestamps
    public Order(String status, double orderAmount, User user) {
        this.status = status;
        this.orderAmount = orderAmount;
        this.user = user;
    }
}
