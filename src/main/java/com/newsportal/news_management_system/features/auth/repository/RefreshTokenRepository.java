package com.newsportal.news_management_system.features.auth.repository;

public interface RefreshTokenRepository {
    void removeUserRefreshToken(Long userId, String jti);
}
