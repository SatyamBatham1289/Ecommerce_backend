package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET OR CREATE CART
    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {

        return cartService.getOrCreateCart(userId);
    }

    // ADD PRODUCT TO CART
    @PostMapping("/{userId}/add")
    public CartItem addToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return cartService.addToCart(
                userId,
                productId,
                quantity
        );
    }

    // GET CART ITEMS WITH PRODUCT DETAILS
    @GetMapping("/{userId}/items")
    public List<CartItemResponse> getCartItems(
            @PathVariable Long userId) {

        return cartService.getCartItems(userId);
    }

    // UPDATE CART ITEM QUANTITY
    @PutMapping("/{userId}/items/{cartItemId}")
    public CartItem updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {

        return cartService.updateCartItem(
                userId,
                cartItemId,
                quantity
        );
    }

    // REMOVE CART ITEM
    @DeleteMapping("/{userId}/items/{cartItemId}")
    public String removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {

        cartService.removeCartItem(
                userId,
                cartItemId
        );

        return "Cart item removed successfully";
    }

    // CLEAR CART
    @DeleteMapping("/{userId}/clear")
    public String clearCart(
            @PathVariable Long userId) {

        cartService.clearCart(userId);

        return "Cart cleared successfully";
    }
}