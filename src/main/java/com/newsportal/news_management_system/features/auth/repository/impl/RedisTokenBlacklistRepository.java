package com.newsportal.news_management_system.features.auth.repository.impl;

import com.newsportal.news_management_system.features.auth.entity.BlacklistedTokenInfo;
import com.newsportal.news_management_system.features.auth.repository.TokenBlacklistRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisTokenBlacklistRepository implements TokenBlacklistRepository {
    static String BLACKLIST_KEY_PREFIX = "blacklist:jti:";
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addToBlacklist(String jti, BlacklistedTokenInfo tokenInfo, long ttlSeconds) {
        String key = BLACKLIST_KEY_PREFIX + jti;
        redisTemplate.opsForValue().set(key, tokenInfo, ttlSeconds, TimeUnit.SECONDS);
        log.debug("Token blacklisted in Redis: key={}, ttl={}s", key, ttlSeconds);;
    }

    @Override
    public boolean isTokenBlacklisted(String jti) {
        String key = BLACKLIST_KEY_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
