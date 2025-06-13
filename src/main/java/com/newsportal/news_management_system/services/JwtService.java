package com.newsportal.news_management_system.services;

import com.newsportal.news_management_system.models.TokenValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

public interface JwtService {
    String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    long getAccessTokenExpiration();
    String extractJti(String token);
    Long extractUserId(String token);
    Date extractExpiration(String token);
    TokenValidationResult validateAndExtractClaims(String token);
}
