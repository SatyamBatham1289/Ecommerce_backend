package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PlaceOrderRequest;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // PLACE ORDER
    @PostMapping("/place")
    public Order placeOrder(
            @Valid @RequestBody PlaceOrderRequest request) {

        return orderService.placeOrder(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity()
        );
    }

    // CREATE ORDER
    @PostMapping
    public Order createOrder(
            @RequestBody Order order) {

        return orderService.createOrder(order);
    }

    // GET ALL ORDERS
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // GET ORDERS BY USER ID
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(
            @PathVariable Long userId) {

        return orderService.getOrdersByUserId(userId);
    }

    // UPDATE ORDER STATUS
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return orderService.updateOrderStatus(
                id,
                status
        );
    }

    // CANCEL ORDER
    @PutMapping("/{id}/cancel")
    public Order cancelOrder(
            @PathVariable Long id) {

        return orderService.cancelOrder(id);
    }

    // GET ORDER BY ID
    @GetMapping("/{id}")
    public Order getOrderById(
            @PathVariable Long id) {

        return orderService.getOrderById(id);
    }

    // CHECKOUT CART
    @PostMapping("/checkout/{userId}")
    public Order checkoutCart(
            @PathVariable Long userId) {

        return orderService.checkoutCart(userId);
    }

    // DELETE ORDER
    @DeleteMapping("/{id}")
    public String deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return "Order deleted successfully";
    }
}