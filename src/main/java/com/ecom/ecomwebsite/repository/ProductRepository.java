package com.ecom.ecomwebsite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.ecomwebsite.model.Product;
import com.ecom.ecomwebsite.model.User;



@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	// Search by product name
    List<Product> findByNameContainingIgnoreCase(String name);

    // Get products by category ID
    List<Product> findByCategoryCategoryId(Long categoryId);
    
    //get product based on seller
    List<Product> findBySeller(User seller);
}
