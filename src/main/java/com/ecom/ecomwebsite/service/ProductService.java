package com.ecom.ecomwebsite.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ecom.ecomwebsite.dto.ProductDTO;
import com.ecom.ecomwebsite.model.Category;
import com.ecom.ecomwebsite.model.Product;
import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;
import com.ecom.ecomwebsite.repository.CategoryRepository;
import com.ecom.ecomwebsite.repository.ProductRepository;
import com.ecom.ecomwebsite.repository.UserRepository;

import java.util.List;
import java.util.Objects;
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

    // ✅ Fetch all products (Public)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ Fetch product by ID (Public)
    public ResponseEntity<?> getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }
    }

    // ✅ Search products by name (Public)
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // ✅ Get products by category (Public)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId);
    }

    // ✅ Get products by seller (Public)
    public ResponseEntity<?> getProductsBySeller(Long sellerId) {
        Optional<User> seller = userRepository.findById(sellerId);

        if (seller.isEmpty() || seller.get().getRole() != RoleType.SELLER) {
            return ResponseEntity.status(404).body("Seller not found with ID: " + sellerId);
        }

        return ResponseEntity.ok(productRepository.findBySeller(seller.get()));
    }

    // ✅ Add new product (Only Sellers/Admin)
    public ResponseEntity<String> addProduct(ProductDTO productDTO) {
        User user = getAuthenticatedUser();

        if (user.getRole() != RoleType.SELLER && user.getRole() != RoleType.ADMIN) {
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
        product.setSeller(user);

        productRepository.save(product);
        return ResponseEntity.ok("Product added successfully by " + user.getRole() + ": " + product.getName());
    }

    // ✅ Update existing product (Only Sellers/Admin)
    public ResponseEntity<String> updateProduct(Long productId, ProductDTO productDTO) {
        if (productId == null) {
            return ResponseEntity.badRequest().body("Error: Product ID cannot be null.");
        }

        Optional<Product> existingProductOpt = productRepository.findById(productId);
        if (existingProductOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productId);
        }

        Product existingProduct = existingProductOpt.get();

        // ✅ Use Objects.nonNull() to avoid NullPointerException
        if (Objects.nonNull(productDTO.getName())) {
            existingProduct.setName(productDTO.getName());
        }
        if (Objects.nonNull(productDTO.getDescription())) {
            existingProduct.setDescription(productDTO.getDescription());
        }
        if (Objects.nonNull(productDTO.getPrice())) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        if (Objects.nonNull(productDTO.getQuantity())) {
            existingProduct.setQuantity(productDTO.getQuantity());
        }
        if (Objects.nonNull(productDTO.getCategoryId())) {
            Optional<Category> categoryOpt = categoryRepository.findById(productDTO.getCategoryId());
            categoryOpt.ifPresent(existingProduct::setCategory);
        }

        productRepository.save(existingProduct);
        return ResponseEntity.ok("Product updated successfully: " + existingProduct.getName());
    }


    // ✅ Delete product (Only Sellers/Admin)
    public ResponseEntity<String> deleteProduct(Long productId) {
        User requestUser = getAuthenticatedUser();
        Optional<Product> existingProduct = productRepository.findById(productId);

        if (existingProduct.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found with ID: " + productId);
        }

        Product product = existingProduct.get();

        // Admins can delete any product, Sellers can only delete their own products
        if (requestUser.getRole() == RoleType.ADMIN || product.getSeller().equals(requestUser)) {
            productRepository.delete(product);
            return ResponseEntity.ok("Product deleted successfully.");
        } else {
            return ResponseEntity.status(403).body("Access Denied: You can only delete your own products.");
        }
    }

    // ✅ Get the currently authenticated user
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
