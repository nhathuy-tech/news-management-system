package com.newsportal.news_management_system.services.impl;

import com.newsportal.news_management_system.models.User;
import com.newsportal.news_management_system.repositories.UserRepository;
import com.newsportal.news_management_system.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        user.setFullName(user.getFullName().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(Instant.now());
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.getEmail().equals(updatedUser.getEmail()) && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        user.setFullName(updatedUser.getFullName());
        user.setRole(updatedUser.getRole());
        user.setIsActive(updatedUser.getIsActive());
        user.setUpdatedDate(Instant.now());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }
}
