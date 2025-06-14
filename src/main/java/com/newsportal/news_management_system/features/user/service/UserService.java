package com.newsportal.news_management_system.features.user.service;

import com.newsportal.news_management_system.features.user.dto.request.CreateUserRequest;
import com.newsportal.news_management_system.features.user.dto.response.CreateUserResponse;
import com.newsportal.news_management_system.features.user.entity.User;

import java.util.List;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
