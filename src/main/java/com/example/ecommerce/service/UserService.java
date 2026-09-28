package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE USER
    public User createUser(User user) {

        // CHECK DUPLICATE EMAIL
        User existingUser =
                userRepository.findByEmail(user.getEmail());

        if (existingUser != null) {
            throw new BusinessException(
                    "Email already registered"
            );
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET USER BY ID
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    // UPDATE USER
    public User updateUser(
            Long id,
            User user) {

        User existingUser =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        existingUser.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        existingUser.setPhone(user.getPhone());

        return userRepository.save(existingUser);
    }

    // DELETE USER
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + id
            );
        }

        userRepository.deleteById(id);
    }

    // LOGIN USER
    public User loginUser(
            String email,
            String password) {

        User user =
                userRepository.findByEmail(email);

        if (user == null) {
            throw new BusinessException(
                    "Invalid email or password"
            );
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new BusinessException(
                    "Invalid email or password"
            );
        }

        return user;
    }
}