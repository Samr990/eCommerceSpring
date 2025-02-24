package com.ecom.ecomwebsite.service;


import com.ecom.ecomwebsite.dto.ProductDTO;
import com.ecom.ecomwebsite.model.Category;
import com.ecom.ecomwebsite.model.Product;
import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;
import com.ecom.ecomwebsite.repository.CategoryRepository;
import com.ecom.ecomwebsite.repository.ProductRepository;
import com.ecom.ecomwebsite.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Fetch all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Fetch product by ID
    public ResponseEntity<?> getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get()); // Returns ResponseEntity<Product>
        } else {
            return ResponseEntity.status(404).body("Product not found with ID: " + id); // Returns ResponseEntity<String>
        }
    }


    // Search products by name
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // Get products by category
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId);
    }

    // Get products by seller
    public ResponseEntity<?> getProductsBySeller(String sellerEmail) {
        Optional<User> seller = userRepository.findByEmail(sellerEmail);

        if (seller.isEmpty() || seller.get().getRole() != RoleType.SELLER) {
            return ResponseEntity.status(404).body("Seller not found with email: " + sellerEmail);
        }

        return ResponseEntity.ok(productRepository.findBySeller(seller.get()));
    }

    // Add new product (Only Seller/Admin)
    public ResponseEntity<String> addProduct(ProductDTO productDTO, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty() || (user.get().getRole() != RoleType.SELLER && user.get().getRole() != RoleType.ADMIN)) {
            return ResponseEntity.status(403).body("Access Denied: Only Sellers and Admins can add products.");
        }

        Optional<Category> category = categoryRepository.findById(productDTO.getCategoryId());
        if (category.isEmpty()) {
            return ResponseEntity.status(404).body("Category not found.");
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setCategory(category.get());
        product.setSeller(user.get());

        productRepository.save(product);
        return ResponseEntity.ok("Product added successfully by " + user.get().getRole() + ": " + product.getName());
    }

    // Update existing product (Only Seller/Admin)
    public ResponseEntity<String> updateProduct(Long productId, ProductDTO productDTO, String userEmail) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (existingProduct.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productId);
        }
        if (user.isEmpty()) {
            return ResponseEntity.status(403).body("Access Denied: User not found.");
        }

        Product product = existingProduct.get();
        User requestUser = user.get();

        if (requestUser.getRole() == RoleType.ADMIN || product.getSeller().equals(requestUser)) {
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setQuantity(productDTO.getQuantity());
            product.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElse(product.getCategory()));

            productRepository.save(product);
            return ResponseEntity.ok("Product updated successfully by " + requestUser.getRole() + ": " + product.getName());
        } else {
            return ResponseEntity.status(403).body("Access Denied: You can only update your own products.");
        }
    }

    // Delete product (Only Seller/Admin)
    public ResponseEntity<String> deleteProduct(Long productId, String userEmail) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (existingProduct.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productId);
        }
        if (user.isEmpty()) {
            return ResponseEntity.status(403).body("Access Denied: User not found.");
        }

        Product product = existingProduct.get();
        User requestUser = user.get();

        if (requestUser.getRole() == RoleType.ADMIN || product.getSeller().equals(requestUser)) {
            productRepository.delete(product);
            return ResponseEntity.ok("Product deleted successfully.");
        } else {
            return ResponseEntity.status(403).body("Access Denied: You can only delete your own products.");
        }
    }
}
