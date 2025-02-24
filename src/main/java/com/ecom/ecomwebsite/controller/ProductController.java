package com.ecom.ecomwebsite.controller;

import com.ecom.ecomwebsite.dto.ProductDTO;
import com.ecom.ecomwebsite.model.Product;
import com.ecom.ecomwebsite.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody ProductDTO productDTO, @RequestParam String userEmail) {
        return productService.addProduct(productDTO, userEmail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO, @RequestParam String userEmail) {
        return productService.updateProduct(id, productDTO, userEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id, @RequestParam String userEmail) {
        return productService.deleteProduct(id, userEmail);
    }
}
