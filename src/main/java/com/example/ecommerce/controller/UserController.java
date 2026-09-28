package com.example.ecommerce.controller;

import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.LoginResponse;
import com.example.ecommerce.dto.UserRequest;
import com.example.ecommerce.dto.UserResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    // CREATE USER
    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody UserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());

        User savedUser =
                userService.createUser(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone()
        );
    }

    // GET ALL USERS
    @GetMapping
    public List<UserResponse> getAllUsers() {

        List<User> users =
                userService.getAllUsers();

        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone()
                ))
                .toList();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable Long id) {

        User user =
                userService.getUserById(id);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    // UPDATE USER
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());

        User updatedUser =
                userService.updateUser(id, user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone()
        );
    }

    // LOGIN
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        User user =
                userService.loginUser(
                        request.getEmail(),
                        request.getPassword()
                );

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return new LoginResponse(token);
    }

    // DELETE USER
    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return "User deleted successfully";
    }
}