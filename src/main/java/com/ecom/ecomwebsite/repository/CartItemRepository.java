package com.ecom.ecomwebsite.repository;

import com.ecom.ecomwebsite.model.Cart;
import com.ecom.ecomwebsite.model.CartItem;
import com.ecom.ecomwebsite.model.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	 List<CartItem> findByCart(Cart cart);
	 Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
