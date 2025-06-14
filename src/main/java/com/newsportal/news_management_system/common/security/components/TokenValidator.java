package com.newsportal.news_management_system.common.security.components;

import com.newsportal.news_management_system.common.dto.TokenValidationResult;
import com.newsportal.news_management_system.features.auth.repository.TokenBlacklistRepository;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenValidator {
    @Value("${jwt.secret:mySecretKey}")
    private String secretKey;
    private JWSVerifier verifier;

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @PostConstruct
    public void init() {
        try {
            byte[] secret = secretKey.getBytes();
            this.verifier = new MACVerifier(secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JWT service", e);
        }
    }

    public TokenValidationResult validate(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);

            if (!jwsObject.verify(verifier)) {
                return TokenValidationResult.invalid("Invalid JWT signature");
            }

            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            // Extract only validation-related claims
            String jti = claims.getJWTID();
            Date expiration = claims.getExpirationTime();

            // Validate expiration
            if (isTokenExpired(expiration)) {
                return TokenValidationResult.invalid("Token has expired");
            }

            // Check if the token is blacklisted
            if (tokenBlacklistRepository.isTokenBlacklisted(jti)) {
                return TokenValidationResult.invalid("Token is blacklisted");
            }

            log.info("Token validated successfully: jti={}, expiration={}", jti, expiration);

            return TokenValidationResult.valid(jti, expiration);

        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            return TokenValidationResult.invalid("Failed to validate token: " + e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String jti) {
        try {
            return tokenBlacklistRepository.isTokenBlacklisted(jti);
        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(Date expiration) {
        return expiration == null || expiration.before(new Date());
    }
}
