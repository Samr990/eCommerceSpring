package com.ecom.ecomwebsite.controller;

import com.ecom.ecomwebsite.dto.ProductDTO;
import com.ecom.ecomwebsite.model.Product;
import com.ecom.ecomwebsite.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ Get all products (Public)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ Get product by ID (Public)
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // ✅ Search products by name (Public)
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    // ✅ Get products by category (Public)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    // ✅ Get products by seller (Public)
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getProductsBySeller(@PathVariable Long sellerId) {
        return productService.getProductsBySeller(sellerId);
    }

    // ✅ Add new product (Only Sellers/Admins)
    @PostMapping("/add")
    public ResponseEntity<String> addProduct(
            @RequestBody ProductDTO productDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return productService.addProduct(productDTO);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable Long productId, 
            @RequestBody ProductDTO productDTO) {
        
        if (productId == null) {
            return ResponseEntity.badRequest().body("Error: Product ID cannot be null.");
        }

        return productService.updateProduct(productId, productDTO);
    }
    // ✅ Delete product (Only Sellers/Admins)
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return productService.deleteProduct(productId);
    }
}
