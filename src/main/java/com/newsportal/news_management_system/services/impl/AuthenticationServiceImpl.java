package com.newsportal.news_management_system.services.impl;

import com.newsportal.news_management_system.dtos.request.LoginRequest;
import com.newsportal.news_management_system.dtos.request.LogoutRequest;
import com.newsportal.news_management_system.dtos.request.RefreshTokenRequest;
import com.newsportal.news_management_system.dtos.response.AuthenticationResponse;
import com.newsportal.news_management_system.services.AuthenticationService;
import com.newsportal.news_management_system.security.authorization.CustomUserPrincipal;
import com.newsportal.news_management_system.services.JwtService;
import com.newsportal.news_management_system.services.TokenBlacklistService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    AuthenticationManager authenticationManager;
    JwtService jwtService;
    TokenBlacklistService tokenBlacklistService;

    @Override
    public AuthenticationResponse authenticate(LoginRequest request) {
        // Step 2-3: Create UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword());

        // Step 4: Authenticate with AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(authRequest);

        // Step 6-7: Get user details (already done by AuthenticationProvider)
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

        AuthenticationResponse response = generateTokenResponse(userPrincipal);

        log.info("User authenticated successfully: {}", userPrincipal.getEmail());
        return response;
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }

    @Override
    public void logout(LogoutRequest request) {
        String refreshToken = request.getRefreshToken();

        try {
            tokenBlacklistService.blacklistToken(refreshToken, "refresh"); // SECURE: Token is validated inside blacklistToken method
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new RuntimeException("Logout failed");
        }
    }

    private AuthenticationResponse generateTokenResponse(CustomUserPrincipal userPrincipal) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userPrincipal.getUserId());
        extraClaims.put("fullName", userPrincipal.getFullName());

        List<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        extraClaims.put("authorities", authorities);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(extraClaims, userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        // Create user info
        AuthenticationResponse.UserInfo userInfo = AuthenticationResponse.UserInfo.builder()
                .userId(userPrincipal.getUserId())
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .roles(authorities)
                .isActive(userPrincipal.getIsActive())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(userInfo)
                .build();
    }
}
