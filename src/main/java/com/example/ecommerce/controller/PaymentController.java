package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // CREATE PAYMENT
    @PostMapping
    public Payment createPayment(
            @RequestParam Long orderId,
            @RequestParam String paymentMethod) {

        return paymentService.createPayment(
                orderId,
                paymentMethod
        );
    }

    // UPDATE PAYMENT STATUS
    @PutMapping("/{paymentId}/status")
    public Payment updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam String paymentStatus) {

        return paymentService.updatePaymentStatus(
                paymentId,
                paymentStatus
        );
    }

    // GET ALL PAYMENTS
    @GetMapping
    public List<Payment> getAllPayments() {

        return paymentService.getAllPayments();
    }

    // GET PAYMENTS BY ORDER ID
    @GetMapping("/order/{orderId}")
    public List<Payment> getPaymentsByOrderId(
            @PathVariable Long orderId) {

        return paymentService.getPaymentsByOrderId(orderId);
    }
}