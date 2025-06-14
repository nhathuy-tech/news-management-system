package com.newsportal.news_management_system.common.security.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtClaimsExtractor {
    private final JwtTokenParser tokenParser;

    public String extractJti(String token) {
        return tokenParser.extractClaim(token, JWTClaimsSet::getJWTID);
    }

    public String extractUsername(String token) {
        return tokenParser.extractClaim(token, JWTClaimsSet::getSubject);
    }

    public Long extractUserId(String token) {
        return tokenParser.extractClaim(token, claims ->
                Optional.ofNullable(claims.getClaim("userId"))
                        .map(val -> ((Number) val).longValue())
                        .orElse(null)
        );
    }

    public Date extractExpiration(String token) {
        return tokenParser.extractClaim(token, JWTClaimsSet::getExpirationTime);
    }
}
