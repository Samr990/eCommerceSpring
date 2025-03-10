package com.ecom.ecomwebsite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.ecomwebsite.model.Order;
import com.ecom.ecomwebsite.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

	 List<Order> findByUser(User user);
}
