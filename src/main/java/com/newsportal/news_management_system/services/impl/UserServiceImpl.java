package com.newsportal.news_management_system.services.impl;

import com.newsportal.news_management_system.dtos.request.CreateUserRequest;
import com.newsportal.news_management_system.dtos.response.CreateUserResponse;
import com.newsportal.news_management_system.exceptions.AppException;
import com.newsportal.news_management_system.exceptions.ErrorCode;
import com.newsportal.news_management_system.models.Role;
import com.newsportal.news_management_system.models.User;
import com.newsportal.news_management_system.repositories.RoleRepository;
import com.newsportal.news_management_system.repositories.UserRepository;
import com.newsportal.news_management_system.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Set<Role> roles = new HashSet<>();
        for (Integer roleId : request.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
            roles.add(role);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getUserId());

        return CreateUserResponse.builder()
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .roleNames(savedUser.getRoleNames())
                .isActive(savedUser.getIsActive())
                .createdDate(savedUser.getCreatedDate())
                .build();
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
        //user.setRole(updatedUser.getRole());
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
