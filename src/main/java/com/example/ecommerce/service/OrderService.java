package com.example.ecommerce.service;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    // PLACE ORDER
    public Order placeOrder(
            Long userId,
            Long productId,
            Integer quantity) {

        // CHECK USER
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        // GET PRODUCT
        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: " + productId
                                )
                        );

        // CHECK QUANTITY
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(
                    "Quantity must be greater than 0"
            );
        }

        // CHECK STOCK
        if (product.getQuantity() < quantity) {
            throw new BusinessException(
                    "Insufficient stock"
            );
        }

        // CALCULATE TOTAL
        Double totalAmount =
                product.getPrice() * quantity;

        // CREATE ORDER
        Order order = new Order();

        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");

        Order savedOrder =
                orderRepository.save(order);

        // CREATE ORDER ITEM
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(savedOrder);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice());

        orderItemRepository.save(orderItem);

        // UPDATE PRODUCT STOCK
        product.setQuantity(
                product.getQuantity() - quantity
        );

        productRepository.save(product);

        return savedOrder;
    }

    // CREATE ORDER
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // GET ALL ORDERS
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // GET ORDERS BY USER ID
    public List<Order> getOrdersByUserId(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        return orderRepository.findByUserId(userId);
    }

    // UPDATE ORDER STATUS
    public Order updateOrderStatus(
            Long id,
            String status) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with id: " + id
                                )
                        );

        if (status == null || status.isBlank()) {
            throw new BusinessException(
                    "Order status is required"
            );
        }

        if (!status.equalsIgnoreCase("PLACED")
                && !status.equalsIgnoreCase("PAID")
                && !status.equalsIgnoreCase("SHIPPED")
                && !status.equalsIgnoreCase("DELIVERED")
                && !status.equalsIgnoreCase("CANCELLED")) {

            throw new BusinessException(
                    "Invalid order status"
            );
        }

        order.setStatus(status.toUpperCase());

        return orderRepository.save(order);
    }

    // CANCEL ORDER + RESTORE STOCK
    @Transactional
    public Order cancelOrder(Long orderId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with id: " + orderId
                                )
                        );

        // CHECK ALREADY CANCELLED
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(
                    "Order is already cancelled"
            );
        }

        // GET ORDER ITEMS
        List<OrderItem> orderItems =
                orderItemRepository.findByOrder_Id(orderId);

        // RESTORE STOCK
        for (OrderItem orderItem : orderItems) {

            Product product =
                    productRepository.findById(
                            orderItem.getProductId()
                    ).orElse(null);

            if (product != null) {

                product.setQuantity(
                        product.getQuantity()
                                + orderItem.getQuantity()
                );

                productRepository.save(product);
            }
        }

        // UPDATE ORDER STATUS
        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }

    // GET ORDER BY ID
    public Order getOrderById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + id
                        )
                );
    }

    // CHECKOUT CART AND CREATE ORDER
    @Transactional
    public Order checkoutCart(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        // GET CART
        Cart cart =
                cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new BusinessException(
                    "Cart not found"
            );
        }

        // GET CART ITEMS
        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cart.getId()
                );

        if (cartItems.isEmpty()) {
            throw new BusinessException(
                    "Cart is empty"
            );
        }

        // CALCULATE TOTAL
        double totalAmount = 0.0;

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

            if (cartItem.getQuantity() == null
                    || cartItem.getQuantity() <= 0) {

                throw new BusinessException(
                        "Cart item quantity must be greater than 0"
                );
            }

            if (product.getQuantity()
                    < cartItem.getQuantity()) {

                throw new BusinessException(
                        "Insufficient stock for product: "
                                + product.getId()
                );
            }

            totalAmount =
                    totalAmount
                            + (product.getPrice()
                            * cartItem.getQuantity());
        }

        // CREATE ORDER
        Order order = new Order();

        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");

        Order savedOrder =
                orderRepository.save(order);

        // CREATE ORDER ITEMS + UPDATE STOCK
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

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(savedOrder);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItemRepository.save(orderItem);

            // REDUCE STOCK
            product.setQuantity(
                    product.getQuantity()
                            - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        // CLEAR CART
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }

    // DELETE ORDER
    public void deleteOrder(Long id) {

        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Order not found with id: " + id
            );
        }

        orderRepository.deleteById(id);
    }
}