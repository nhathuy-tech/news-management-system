package com.newsportal.news_management_system.features.auth.service.impl;

import com.newsportal.news_management_system.features.auth.service.AuthenticationService;
import com.newsportal.news_management_system.common.security.components.TokenBlacklistManager;
import com.newsportal.news_management_system.common.security.components.TokenResponseBuilder;
import com.newsportal.news_management_system.features.auth.dto.request.LoginRequest;
import com.newsportal.news_management_system.features.auth.dto.request.LogoutRequest;
import com.newsportal.news_management_system.features.auth.dto.request.RefreshTokenRequest;
import com.newsportal.news_management_system.features.auth.dto.response.AuthenticationResponse;
import com.newsportal.news_management_system.common.dto.TokenValidationResult;
import com.newsportal.news_management_system.features.user.entity.User;
import com.newsportal.news_management_system.common.security.CustomUserPrincipal;
import com.newsportal.news_management_system.common.security.components.TokenValidationHandler;
import com.newsportal.news_management_system.common.security.components.UserResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

// Main Authentication Service - Orchestrator
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    AuthenticationManager authenticationManager;

    TokenResponseBuilder tokenResponseBuilder;
    TokenBlacklistManager tokenBlacklistManager;
    TokenValidationHandler tokenValidationHandler;
    UserResolver userResolver;

    @Override
    public AuthenticationResponse authenticate(LoginRequest request) {
        Authentication authentication = performAuthentication(request);

        // Step 6-7: Get user details (already done by AuthenticationProvider)
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

        AuthenticationResponse response = tokenResponseBuilder.buildResponse(userPrincipal);

        log.info("User authenticated successfully: {}", userPrincipal.getEmail());
        return response;
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        TokenValidationResult validation = tokenValidationHandler.validateAndThrow(request.getRefreshToken());
        User user = userResolver.resolveUserFromToken(request.getRefreshToken());

        tokenBlacklistManager.blacklistToken(user, validation);

        CustomUserPrincipal userPrincipal = CustomUserPrincipal.create(user);

        return tokenResponseBuilder.buildResponse(userPrincipal);
    }

    @Override
    public void logout(LogoutRequest request) {
        TokenValidationResult validation = tokenValidationHandler.validateAndThrow(request.getRefreshToken());
        User user = userResolver.resolveUserFromToken(request.getRefreshToken());
        tokenBlacklistManager.blacklistToken(user, validation);
        log.info("User logged out successfully: {}", user.getEmail());
    }

    private Authentication performAuthentication(LoginRequest request) {
        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword());

        return authenticationManager.authenticate(authRequest);
    }
}
