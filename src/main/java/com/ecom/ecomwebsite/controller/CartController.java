package com.ecom.ecomwebsite.controller;

import com.ecom.ecomwebsite.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    //  Get Cart for a User
    @GetMapping
    public ResponseEntity<?> getCart(@RequestParam String userEmail) {
        return cartService.getCartByUser(userEmail);
    }

    //  Add Product to Cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam String userEmail) {
        return cartService.addToCart(productId, quantity, userEmail);
    }

    //  Update Quantity of a Product in the Cart
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(
            @RequestParam Long itemId,
            @RequestParam int newQuantity,
            @RequestParam String userEmail) {
        return cartService.updateCartItem(itemId, newQuantity, userEmail);
    }

    //  Remove Product from Cart
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItemFromCart(
            @RequestParam Long itemId,
            @RequestParam String userEmail) {
        return cartService.removeItemFromCart(itemId, userEmail);
    }

    // Checkout (Clears Cart After Payment)
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestParam String userEmail) {
        return cartService.checkout(userEmail);
    }
}
