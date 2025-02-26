package com.ecom.ecomwebsite.service;

import com.ecom.ecomwebsite.dto.CartDTO;
import com.ecom.ecomwebsite.dto.CartItemDTO;
import com.ecom.ecomwebsite.model.*;
import com.ecom.ecomwebsite.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, UserRepository userRepository,
                       OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // ✅ Get Authenticated User
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // ✅ Convert Cart to DTO
    private CartDTO convertToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setCartTotal(cart.getCartTotal());

        List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        cartDTO.setCartItems(cartItemDTOs);
        return cartDTO;
    }

    // ✅ Convert CartItem to DTO
    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setItemId(cartItem.getItemId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setProductPrice(cartItem.getProductPrice());
        dto.setProductId(cartItem.getProduct().getProductId());
        dto.setProductName(cartItem.getProduct().getName());
        return dto;
    }

    // ✅ Get User's Cart
    public ResponseEntity<?> getCart() {
        User user = getAuthenticatedUser();

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCartTotal(0.0);
            return cartRepository.save(newCart);
        });

        return ResponseEntity.ok(convertToCartDTO(cart));
    }

    // ✅ Add Product to Cart
    public ResponseEntity<?> addToCart(Long productId, int quantity) {
        User user = getAuthenticatedUser();
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found.");
        }

        Product product = productOpt.get();
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCartTotal(0.0);
            return cartRepository.save(newCart);
        });

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setProductPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getPrice() * quantity);
            cartItemRepository.save(cartItem);
        }

        updateCartTotal(cart);
        return ResponseEntity.ok("Product added to cart.");
    }

    // ✅ Update Cart Item Quantity
    public ResponseEntity<?> updateCartItem(Long itemId, int newQuantity) {
        User user = getAuthenticatedUser();
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(itemId);

        if (cartItemOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Cart item not found.");
        }

        CartItem cartItem = cartItemOpt.get();
        Cart cart = cartItem.getCart();

        if (newQuantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
            cartItem.setProductPrice(cartItem.getProduct().getPrice() * newQuantity);
            cartItemRepository.save(cartItem);
        }

        updateCartTotal(cart);
        return ResponseEntity.ok("Cart item updated.");
    }

    // ✅ Remove Item from Cart
    public ResponseEntity<?> removeItemFromCart(Long itemId) {
        User user = getAuthenticatedUser();
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(itemId);

        if (cartItemOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Cart item not found.");
        }

        CartItem cartItem = cartItemOpt.get();
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);

        updateCartTotal(cart);
        return ResponseEntity.ok("Item removed from cart.");
    }

    // ✅ Checkout
    @Transactional
    public ResponseEntity<String> checkout() {
        User user = getAuthenticatedUser();
        Optional<Cart> cartOpt = cartRepository.findByUser(user);

        if (cartOpt.isEmpty() || cartOpt.get().getCartItems().isEmpty()) {
            return ResponseEntity.status(400).body("Cart is empty.");
        }

        Cart cart = cartOpt.get();
        Order newOrder = new Order("PAYMENT_PENDING", cart.getCartTotal(), user);
        Order savedOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProductPrice());
            return orderItem;
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.setCartTotal(0.0);
        cartRepository.save(cart);

        return ResponseEntity.ok("Order created! Order ID: " + savedOrder.getOrderId() + ". Please complete the payment.");
    }

    // ✅ Helper: Update Cart Total
    private void updateCartTotal(Cart cart) {
        double newTotal = cartItemRepository.findByCart(cart).stream()
                .mapToDouble(CartItem::getProductPrice)
                .sum();
        cart.setCartTotal(newTotal);
        cartRepository.save(cart);
    }
}
