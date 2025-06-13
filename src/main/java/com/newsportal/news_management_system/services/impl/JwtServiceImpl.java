package com.newsportal.news_management_system.services.impl;

import com.newsportal.news_management_system.models.TokenValidationResult;
import com.newsportal.news_management_system.services.JwtService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
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
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    @Override
    public long getAccessTokenExpiration() {
        return jwtExpiration;
    }

    @Override
    public String extractJti(String token) {
        return extractClaim(token, JWTClaimsSet::getJWTID);
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaim(token, claims ->
                Optional.ofNullable(claims.getClaim("userId"))
                        .map(val -> ((Number) val).longValue())
                        .orElse(null)
        );
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, JWTClaimsSet::getExpirationTime);
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private JWTClaimsSet extractAllClaims(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            if (!jwsObject.verify(verifier)) {
                throw new RuntimeException("JWT signature verification failed");
            }
            return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration);

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256); //Header with HS256 algorithm
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder() // Create JWT claims set builder
                    .subject(userDetails.getUsername())
                    .issueTime(now)
                    .expirationTime(expiryDate)
                    .jwtID(UUID.randomUUID().toString());
            if (extraClaims != null) {
                extraClaims.forEach(claimsBuilder::claim);
            }
            JWTClaimsSet claimsSet = claimsBuilder.build(); // Build the claims set
            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload); // Create JWS object with header and payload
            jwsObject.sign(signer); // Sign the JWS object with the signer
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    @Override
    public TokenValidationResult validateAndExtractClaims(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            if (!jwsObject.verify(verifier)) {
                return TokenValidationResult.invalid("Invalid JWT signature");
            }

            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            String jti = claims.getJWTID();
            Long userId = Optional.ofNullable(claims.getClaim("userId"))
                    .map(val -> ((Number) val).longValue())
                    .orElse(null);
            String username = claims.getSubject();
            Date expiration = claims.getExpirationTime();

            if (expiration == null || expiration.before(new Date())) {
                return TokenValidationResult.invalid("Token has expired");
            }

            return TokenValidationResult.valid(jti, userId, username, expiration);
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            return TokenValidationResult.invalid("Failed to validate token: " + e.getMessage());
        }
    }
}
