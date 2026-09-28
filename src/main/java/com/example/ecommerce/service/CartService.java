package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // GET OR CREATE CART
    public Cart getOrCreateCart(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        Cart cart =
                cartRepository.findByUserId(userId);

        if (cart == null) {

            cart = new Cart();
            cart.setUserId(userId);

            cart = cartRepository.save(cart);
        }

        return cart;
    }

    // ADD PRODUCT TO CART
    public CartItem addToCart(
            Long userId,
            Long productId,
            Integer quantity) {

        Cart cart = getOrCreateCart(userId);

        if (quantity == null || quantity <= 0) {
            throw new BusinessException(
                    "Quantity must be greater than 0"
            );
        }

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );

        if (product.getQuantity() < quantity) {
            throw new BusinessException(
                    "Insufficient stock"
            );
        }

        CartItem existingItem =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        productId
                );

        if (existingItem != null) {

            int newQuantity =
                    existingItem.getQuantity() + quantity;

            if (product.getQuantity() < newQuantity) {
                throw new BusinessException(
                        "Insufficient stock"
                );
            }

            existingItem.setQuantity(newQuantity);

            return cartItemRepository.save(existingItem);
        }

        CartItem cartItem = new CartItem();

        cartItem.setCartId(cart.getId());
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }

    // GET CART ITEMS WITH PRODUCT DETAILS
    public List<CartItemResponse> getCartItems(Long userId) {

        Cart cart = getOrCreateCart(userId);

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(cart.getId());

        List<CartItemResponse> response =
                new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product =
                    productRepository.findById(
                            cartItem.getProductId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found with id: "
                                            + cartItem.getProductId()
                            )
                    );

            Double total =
                    product.getPrice()
                            * cartItem.getQuantity();

            CartItemResponse cartItemResponse =
                    new CartItemResponse(
                            cartItem.getId(),
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            cartItem.getQuantity(),
                            total
                    );

            response.add(cartItemResponse);
        }

        return response;
    }

    // UPDATE CART ITEM QUANTITY
    public CartItem updateCartItem(
            Long userId,
            Long cartItemId,
            Integer quantity) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found with id: "
                                                + cartItemId
                                )
                        );

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new BusinessException(
                    "Cart item does not belong to this user"
            );
        }

        if (quantity == null || quantity <= 0) {
            throw new BusinessException(
                    "Quantity must be greater than 0"
            );
        }

        Product product =
                productRepository.findById(
                        cartItem.getProductId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: "
                                        + cartItem.getProductId()
                        )
                );

        if (product.getQuantity() < quantity) {
            throw new BusinessException(
                    "Insufficient stock"
            );
        }

        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }

    // REMOVE CART ITEM
    public void removeCartItem(
            Long userId,
            Long cartItemId) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found with id: "
                                                + cartItemId
                                )
                        );

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new BusinessException(
                    "Cart item does not belong to this user"
            );
        }

        cartItemRepository.delete(cartItem);
    }

    // CLEAR CART
    public void clearCart(Long userId) {

        Cart cart = getOrCreateCart(userId);

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cart.getId()
                );

        cartItemRepository.deleteAll(cartItems);
    }
}