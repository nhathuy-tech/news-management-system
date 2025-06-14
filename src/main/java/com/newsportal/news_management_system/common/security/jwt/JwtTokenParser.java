package com.newsportal.news_management_system.common.security.jwt;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtDecoderInitializationException;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenParser {
    private final JwtProperties jwtProperties;
    private JWSVerifier verifier;

    @PostConstruct
    public void init() {
        try {
            byte[] secret = jwtProperties.getSecret().getBytes();
            this.verifier = new MACVerifier(secret);
        } catch (Exception e) {
            throw new JwtDecoderInitializationException("Failed to initialize JWT verifier", e);
        }
    }

    public JWTClaimsSet parseAndVerify(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);

            if (!jwsObject.verify(verifier)) {
                throw new RuntimeException("JWT signature verification failed");
            }

            return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        } catch (Exception e) {
            log.error("Error parsing JWT token", e);
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsExtractor) {
        JWTClaimsSet claims = parseAndVerify(token);
        return claimsExtractor.apply(claims);
    }
}
