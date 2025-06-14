package com.newsportal.news_management_system.features.auth.service;

import com.newsportal.news_management_system.common.dto.TokenValidationResult;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;

public interface JwtService {
    String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    long getAccessTokenExpiration();
    String extractJti(String token);
    String extractUsername(String token);
    Long extractUserId(String token);
    Date extractExpiration(String token);
    TokenValidationResult validateAndExtractClaims(String token);
}
