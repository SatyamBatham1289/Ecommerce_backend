# E-Commerce Backend API

A backend E-Commerce application built using Spring Boot, MySQL, JPA/Hibernate, Spring Security, JWT Authentication, REST APIs, and Swagger/OpenAPI.

## Project Overview

This project provides backend APIs for an E-Commerce application.

Users can register and login, products can be managed, users can add products to cart, place orders, checkout the cart, and create/update payments.

## Technologies Used

- Java
- Spring Boot
- Spring MVC / REST API
- Spring Data JPA
- Hibernate
- MySQL
- Spring Security
- JWT Authentication
- Maven
- Swagger / OpenAPI
- Postman

## Project Structure

```text
src/main/java/com/example/ecommerce
│
├── config
│   ├── SecurityConfig.java
│   └── OpenAPIConfig.java
│
├── controller
│   ├── UserController.java
│   ├── ProductController.java
│   ├── OrderController.java
│   ├── OrderItemController.java
│   ├── CartController.java
│   └── PaymentController.java
│
├── dto
│   ├── UserRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ProductRequest.java
│   ├── ProductResponse.java
│   ├── PlaceOrderRequest.java
│   └── CartItemResponse.java
│
├── entity
│   ├── User.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Cart.java
│   ├── CartItem.java
│   └── Payment.java
│
├── exception
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
│
├── repository
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   ├── OrderItemRepository.java
│   ├── CartRepository.java
│   ├── CartItemRepository.java
│   └── PaymentRepository.java
│
├── security
│   ├── JwtService.java
│   └── JwtAuthenticationFilter.java
│
└── service
    ├── UserService.java
    ├── ProductService.java
    ├── OrderService.java
    ├── OrderItemService.java
    ├── CartService.java
    └── PaymentService.java