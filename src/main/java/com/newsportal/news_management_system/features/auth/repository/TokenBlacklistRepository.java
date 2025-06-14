package com.newsportal.news_management_system.features.auth.repository;

import com.newsportal.news_management_system.features.auth.entity.BlacklistedTokenInfo;

public interface TokenBlacklistRepository {
    void addToBlacklist(String jti, BlacklistedTokenInfo tokenInfo, long ttlSeconds);
    boolean isTokenBlacklisted(String jti);
}
