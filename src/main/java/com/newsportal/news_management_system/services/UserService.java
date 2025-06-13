package com.newsportal.news_management_system.services;

import com.newsportal.news_management_system.dtos.request.CreateUserRequest;
import com.newsportal.news_management_system.dtos.response.CreateUserResponse;
import com.newsportal.news_management_system.models.User;

import java.util.List;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
