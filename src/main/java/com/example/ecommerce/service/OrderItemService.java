package com.example.ecommerce.service;

import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(
            OrderItemRepository orderItemRepository) {

        this.orderItemRepository = orderItemRepository;
    }

    // CREATE ORDER ITEM
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    // GET ALL ORDER ITEMS
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // GET ORDER ITEM BY ID
    public OrderItem getOrderItemById(Long id) {

        return orderItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order item not found with id: " + id
                        )
                );
    }

    // DELETE ORDER ITEM
    public void deleteOrderItem(Long id) {

        if (!orderItemRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Order item not found with id: " + id
            );
        }

        orderItemRepository.deleteById(id);
    }
}