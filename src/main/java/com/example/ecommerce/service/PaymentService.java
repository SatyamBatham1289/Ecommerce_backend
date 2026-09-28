package com.example.ecommerce.service;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    // CREATE PAYMENT
    public Payment createPayment(
            Long orderId,
            String paymentMethod) {

        // CHECK ORDER
        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Order not found"
                                )
                        );

        // CHECK PAYMENT METHOD
        if (paymentMethod == null
                || (!paymentMethod.equalsIgnoreCase("COD")
                && !paymentMethod.equalsIgnoreCase("UPI")
                && !paymentMethod.equalsIgnoreCase("CARD"))) {

            throw new BusinessException(
                    "Invalid payment method"
            );
        }

        // CREATE PAYMENT
        Payment payment = new Payment();

        payment.setOrderId(orderId);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(
                paymentMethod.toUpperCase()
        );
        payment.setPaymentStatus("PENDING");

        return paymentRepository.save(payment);
    }

    // UPDATE PAYMENT STATUS
    @Transactional
    public Payment updatePaymentStatus(
            Long paymentId,
            String paymentStatus) {

        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Payment not found"
                                )
                        );

        // CHECK PAYMENT STATUS
        if (paymentStatus == null
                || (!paymentStatus.equalsIgnoreCase("PENDING")
                && !paymentStatus.equalsIgnoreCase("PAID")
                && !paymentStatus.equalsIgnoreCase("FAILED"))) {

            throw new BusinessException(
                    "Invalid payment status"
            );
        }

        String status =
                paymentStatus.toUpperCase();

        payment.setPaymentStatus(status);

        // UPDATE ORDER AFTER SUCCESSFUL PAYMENT
        if ("PAID".equals(status)) {

            Order order =
                    orderRepository.findById(
                            payment.getOrderId()
                    ).orElseThrow(() ->
                            new BusinessException(
                                    "Order not found"
                            )
                    );

            // CANCELLED ORDER CANNOT BE PAID
            if ("CANCELLED".equalsIgnoreCase(
                    order.getStatus())) {

                throw new BusinessException(
                        "Cancelled order cannot be paid"
                );
            }

            order.setStatus("PAID");

            orderRepository.save(order);
        }

        return paymentRepository.save(payment);
    }

    // GET ALL PAYMENTS
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // GET PAYMENTS BY ORDER ID
    public List<Payment> getPaymentsByOrderId(
            Long orderId) {

        return paymentRepository.findByOrderId(orderId);
    }
}