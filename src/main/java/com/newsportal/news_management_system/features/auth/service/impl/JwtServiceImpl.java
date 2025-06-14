package com.newsportal.news_management_system.features.auth.service.impl;

import com.newsportal.news_management_system.common.security.jwt.JwtClaimsExtractor;
import com.newsportal.news_management_system.common.security.jwt.JwtProperties;
import com.newsportal.news_management_system.common.security.jwt.JwtTokenBuilder;
import com.newsportal.news_management_system.common.security.jwt.JwtTokenValidator;
import com.newsportal.news_management_system.features.auth.service.JwtService;
import com.newsportal.news_management_system.common.dto.TokenValidationResult;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService { //Main JWT Service - Facade Pattern

    private final JwtTokenBuilder tokenBuilder;
    private final JwtClaimsExtractor claimsExtractor;
    private final JwtTokenValidator tokenValidator;
    private final JwtProperties jwtProperties;

    @Value("${jwt.secret:mySecretKey}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:3600}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token-expiration:604800}")
    private long refreshExpiration;

    private JWSSigner signer;
    private JWSVerifier verifier;

    @PostConstruct
    public void init() {
        try {
            byte[] secret = secretKey.getBytes();
            this.signer = new MACSigner(secret);
            this.verifier = new MACVerifier(secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JWT service", e);
        }
    }

    @Override
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return tokenBuilder.buildAccessToken(extraClaims, userDetails);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return tokenBuilder.buildRefreshToken(userDetails);
    }

    @Override
    public long getAccessTokenExpiration() {
        return jwtProperties.getAccessTokenExpiration();
    }

    @Override
    public String extractJti(String token) {
        return claimsExtractor.extractJti(token);
    }

    @Override
    public String extractUsername(String token) {
        return claimsExtractor.extractUsername(token);
    }

    @Override
    public Long extractUserId(String token) {
        return claimsExtractor.extractUserId(token);
    }

    @Override
    public Date extractExpiration(String token) {
        return claimsExtractor.extractExpiration(token);
    }

    @Override
    public TokenValidationResult validateAndExtractClaims(String token) {
        return tokenValidator.validate(token);
    }

}
