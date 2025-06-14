package com.newsportal.news_management_system.features.auth.controller;

import com.newsportal.news_management_system.features.auth.dto.request.LoginRequest;
import com.newsportal.news_management_system.features.auth.dto.request.LogoutRequest;
import com.newsportal.news_management_system.features.auth.dto.request.RefreshTokenRequest;
import com.newsportal.news_management_system.common.dto.ApiResponse;
import com.newsportal.news_management_system.features.auth.dto.response.AuthenticationResponse;
import com.newsportal.news_management_system.features.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request received for userId: {}", request.getRefreshToken());
        AuthenticationResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("Logout request received");
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
