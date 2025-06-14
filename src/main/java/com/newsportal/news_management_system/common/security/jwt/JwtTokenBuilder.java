package com.newsportal.news_management_system.common.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoderInitializationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenBuilder {
    private final JwtProperties jwtProperties;
    private JWSSigner signer;

    @PostConstruct
    public void init() {
        try {
            byte[] secret = jwtProperties.getSecret().getBytes();
            this.signer = new MACSigner(secret);
        } catch (Exception e) {
            throw new JwtDecoderInitializationException("Failed to initialize JWT signer", e);
        }
    }

    public String buildAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtProperties.getAccessTokenExpiration());
    }

    public String buildRefreshToken(UserDetails userDetails) {
        return buildToken(Collections.emptyMap(), userDetails, jwtProperties.getRefreshTokenExpiration());
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plusSeconds(expiration);

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            JWTClaimsSet claimsSet = buildClaimsSet(userDetails, now, expiry, extraClaims);

            JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
            jwsObject.sign(signer);

            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("Error generating JWT token for user: {}", userDetails.getUsername(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    private JWTClaimsSet buildClaimsSet(UserDetails userDetails, Instant issuedAt,
                                        Instant expiry, Map<String, Object> extraClaims) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiry))
                .jwtID(UUID.randomUUID().toString());

        if (extraClaims != null && !extraClaims.isEmpty()) {
            extraClaims.forEach(builder::claim);
        }

        return builder.build();
    }
}
