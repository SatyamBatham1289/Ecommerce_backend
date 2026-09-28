package com.example.ecommerce.controller;

import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.service.OrderItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(
            OrderItemService orderItemService) {

        this.orderItemService = orderItemService;
    }

    // CREATE ORDER ITEM
    @PostMapping
    public OrderItem createOrderItem(
            @RequestBody OrderItem orderItem) {

        return orderItemService.createOrderItem(orderItem);
    }

    // GET ALL ORDER ITEMS
    @GetMapping
    public List<OrderItem> getAllOrderItems() {

        return orderItemService.getAllOrderItems();
    }

    // GET ORDER ITEM BY ID
    @GetMapping("/{id}")
    public OrderItem getOrderItemById(
            @PathVariable Long id) {

        return orderItemService.getOrderItemById(id);
    }

    // DELETE ORDER ITEM
    @DeleteMapping("/{id}")
    public String deleteOrderItem(
            @PathVariable Long id) {

        orderItemService.deleteOrderItem(id);

        return "Order item deleted successfully";
    }
}