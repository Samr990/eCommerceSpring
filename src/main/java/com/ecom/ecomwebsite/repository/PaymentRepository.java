package com.ecom.ecomwebsite.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.ecomwebsite.model.Order;
import com.ecom.ecomwebsite.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	 Optional<Payment> findByOrder(Order order);
}
