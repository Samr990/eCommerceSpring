package com.ecom.ecomwebsite.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String payMethod; // Example: CREDIT_CARD, PAYPAL
    private double payAmt;
    private String payStatus; // PENDING, SUCCESSFUL, FAILED
    private Date paymentDate;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
}
