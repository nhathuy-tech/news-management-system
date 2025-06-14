package com.newsportal.news_management_system.features.auth.service;

import com.newsportal.news_management_system.features.auth.dto.request.LoginRequest;
import com.newsportal.news_management_system.features.auth.dto.request.LogoutRequest;
import com.newsportal.news_management_system.features.auth.dto.request.RefreshTokenRequest;
import com.newsportal.news_management_system.features.auth.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(LoginRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
    void logout(LogoutRequest request);
}
