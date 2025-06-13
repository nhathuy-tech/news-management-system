package com.newsportal.news_management_system.services;

import com.newsportal.news_management_system.exceptions.AppException;
import com.newsportal.news_management_system.exceptions.ErrorCode;
import com.newsportal.news_management_system.models.BlacklistedTokenInfo;
import com.newsportal.news_management_system.models.TokenValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtService jwtService;

    private static final String BLACKLIST_KEY_PREFIX = "blacklist:jti:";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh:jti:";
    private static final String USER_TOKENS_KEY_PREFIX = "user:tokens:";

    public void blacklistToken(String token, String tokenType) {
        // STEP 1: Validate token signature and claims
        TokenValidationResult validation = jwtService.validateAndExtractClaims(token);

        if (!validation.isValid()) {
            log.warn("Attempted to blacklist invalid token: {}", validation.getErrorMessage());
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        try {
            BlacklistedTokenInfo tokenInfo = BlacklistedTokenInfo.builder()
                    .userId(validation.getUserId())
                    .tokenType(tokenType)
                    .blacklistedAt(Instant.now())
                    .expiresAt(validation.getExpiration().toInstant())
                    .build();
            String key = BLACKLIST_KEY_PREFIX + validation.getJti();

            // Calculate TTL (time until token expires)
            long ttlSeconds = Duration.between(Instant.now(), validation.getExpiration().toInstant()).getSeconds();

            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, tokenInfo, ttlSeconds, java.util.concurrent.TimeUnit.SECONDS);
                log.info("Token blacklisted: JTI={}, UserId={}, TTL={}s",
                        validation.getJti(), validation.getUserId(), ttlSeconds);

                if ("refresh".equals(tokenType)) {
                    removeUserRefreshToken(validation.getUserId(), validation.getJti());
                }
            }
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
            throw new AppException(ErrorCode.TOKEN_BLACKLIST_FAILED);
        }
    }

    private void removeUserRefreshToken(Long userId, String jti) {
        String tokenKey = REFRESH_TOKEN_KEY_PREFIX + jti;
        String userTokensKey = USER_TOKENS_KEY_PREFIX + userId;

        redisTemplate.delete(tokenKey);
        redisTemplate.opsForSet().remove(userTokensKey, jti);
    }
}
