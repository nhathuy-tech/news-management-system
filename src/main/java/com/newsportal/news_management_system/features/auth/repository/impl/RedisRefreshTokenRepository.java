package com.newsportal.news_management_system.features.auth.repository.impl;

import com.newsportal.news_management_system.features.auth.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisRefreshTokenRepository implements RefreshTokenRepository {
    static String REFRESH_TOKEN_KEY_PREFIX = "refresh:jti:";
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void removeUserRefreshToken(Long userId, String jti) {
        try {
            String key = REFRESH_TOKEN_KEY_PREFIX + userId;
            redisTemplate.opsForSet().remove(key, jti);

            log.debug("Refresh token removed for user: userId={}, jti={}", userId, jti);
        } catch (Exception e) {
            log.error("Error removing refresh token for user: {}", userId, e);
        }
    }
}
