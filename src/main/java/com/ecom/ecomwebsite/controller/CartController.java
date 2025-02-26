package com.ecom.ecomwebsite.controller;

import com.ecom.ecomwebsite.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ✅ Get Cart for Authenticated User
    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.getCart();
    }

    // ✅ Add Product to Cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.addToCart(productId, quantity);
    }

    // ✅ Update Cart Item Quantity
    @PutMapping("/update/{itemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId,
            @RequestParam int newQuantity,
            @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.updateCartItem(itemId, newQuantity);
    }

    // ✅ Remove Item from Cart
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeItemFromCart(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.removeItemFromCart(itemId);
    }

    // ✅ Checkout Cart
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.checkout();
    }
}
