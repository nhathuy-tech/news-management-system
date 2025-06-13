package com.newsportal.news_management_system.services;

import com.newsportal.news_management_system.dtos.request.LoginRequest;
import com.newsportal.news_management_system.dtos.request.LogoutRequest;
import com.newsportal.news_management_system.dtos.request.RefreshTokenRequest;
import com.newsportal.news_management_system.dtos.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(LoginRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
    void logout(LogoutRequest request);
}
