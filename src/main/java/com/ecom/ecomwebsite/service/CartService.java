package com.ecom.ecomwebsite.service;

import com.ecom.ecomwebsite.dto.CartDTO;
import com.ecom.ecomwebsite.dto.CartItemDTO;
import com.ecom.ecomwebsite.model.*;
import com.ecom.ecomwebsite.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
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

    // Convert Cart to CartDTO
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

    // Convert CartItem to CartItemDTO
    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setItemId(cartItem.getItemId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setProductPrice(cartItem.getProductPrice());
        dto.setProductId(cartItem.getProduct().getProductId());
        dto.setProductName(cartItem.getProduct().getName());
        return dto;
    }

    // Get User's Cart
    public ResponseEntity<?> getCartByUser(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found: " + userEmail);
        }

        Cart cart = cartRepository.findByUser(user.get()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user.get());
            newCart.setCartTotal(0.0);
            return cartRepository.save(newCart);
        });

        // Recalculate cart total
        double recalculatedTotal = cartItemRepository.findByCart(cart).stream()
                .mapToDouble(CartItem::getProductPrice)
                .sum();
        cart.setCartTotal(recalculatedTotal);
        cartRepository.save(cart);

        return ResponseEntity.ok(convertToCartDTO(cart));
    }

    // Add Product to Cart
    public ResponseEntity<?> addToCart(Long productId, int quantity, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        Optional<Product> product = productRepository.findById(productId);

        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        if (product.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found.");
        }

        Cart cart = cartRepository.findByUser(user.get()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user.get());
            newCart.setCartTotal(0.0);
            return cartRepository.save(newCart);
        });

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product.get());
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setProductPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product.get());
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.get().getPrice() * quantity);
            cartItemRepository.save(cartItem);
        }

        double newTotal = cartItemRepository.findByCart(cart).stream()
                .mapToDouble(CartItem::getProductPrice)
                .sum();
        cart.setCartTotal(newTotal);
        cartRepository.save(cart);

        return ResponseEntity.ok("Product added to cart. New cart total: " + newTotal);
    }

    // Update Cart Item Quantity
    public ResponseEntity<?> updateCartItem(Long itemId, int newQuantity, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        Optional<CartItem> cartItem = cartItemRepository.findById(itemId);
        if (cartItem.isEmpty()) {
            return ResponseEntity.status(404).body("Cart item not found.");
        }

        CartItem item = cartItem.get();
        if (newQuantity <= 0) {
            return removeItemFromCart(itemId, userEmail);
        }

        item.setQuantity(newQuantity);
        item.setProductPrice(item.getProduct().getPrice() * newQuantity);
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        double newTotal = cartItemRepository.findByCart(cart).stream()
                .mapToDouble(CartItem::getProductPrice)
                .sum();
        cart.setCartTotal(newTotal);
        cartRepository.save(cart);

        return ResponseEntity.ok("Cart item updated. New cart total: " + newTotal);
    }

    // Remove an Item from Cart
    public ResponseEntity<?> removeItemFromCart(Long itemId, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        Optional<CartItem> cartItem = cartItemRepository.findById(itemId);
        if (cartItem.isEmpty()) {
            return ResponseEntity.status(404).body("Cart item not found.");
        }

        Cart cart = cartItem.get().getCart();
        cartItemRepository.delete(cartItem.get());

        double newTotal = cartItemRepository.findByCart(cart).stream()
                .mapToDouble(CartItem::getProductPrice)
                .sum();
        cart.setCartTotal(newTotal);
        cartRepository.save(cart);

        return ResponseEntity.ok("Item removed from cart. New cart total: " + newTotal);
    }

    // Checkout
    @Transactional
    public ResponseEntity<String> checkout(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        Cart cart = cartRepository.findByUser(user.get()).orElse(null);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.status(400).body("Cart is empty.");
        }

        // ✅ Use Instant.now() for timestamps
        Order newOrder = new Order(
                "PAYMENT_PENDING",
                cart.getCartTotal(),
                user.get()
        );

        final Order savedOrder = orderRepository.save(newOrder);

        // ✅ Save order items
        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProductPrice());
            return orderItem;
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        // ✅ Clear the cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.setCartTotal(0.0);
        cartRepository.save(cart);

        return ResponseEntity.ok("Order created! Order ID: " + savedOrder.getOrderId() + ". Please complete the payment.");
    }
}
