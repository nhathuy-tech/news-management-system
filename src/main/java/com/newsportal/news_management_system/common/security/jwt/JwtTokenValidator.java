package com.newsportal.news_management_system.common.security.jwt;

import com.newsportal.news_management_system.common.dto.TokenValidationResult;
import com.newsportal.news_management_system.common.security.components.TokenValidator;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenValidator {
    private final JwtTokenParser tokenParser;
    private final TokenValidator blacklistValidator;

    public TokenValidationResult validate(String token) {
        try {
            JWTClaimsSet claims = tokenParser.parseAndVerify(token);

            // Check expiration
            Date expiration = claims.getExpirationTime();
            if (isTokenExpired(expiration)) {
                return TokenValidationResult.invalid("Token has expired");
            }

            // Check blacklist
            String jti = claims.getJWTID();
            if (blacklistValidator.isTokenBlacklisted(jti)) {
                return TokenValidationResult.invalid("Token has been revoked");
            }

            log.debug("Token validated successfully: jti={}, username={}",
                    jti, claims.getSubject());

            return TokenValidationResult.valid(jti, expiration);
        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            return TokenValidationResult.invalid("Token validation failed");
        }
    }

    private boolean isTokenExpired(Date expiration) {
        return expiration == null || expiration.before(new Date());
    }
}
