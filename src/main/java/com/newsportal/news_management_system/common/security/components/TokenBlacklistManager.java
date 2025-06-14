package com.newsportal.news_management_system.common.security.components;

import com.newsportal.news_management_system.common.enums.TokenPurpose;
import com.newsportal.news_management_system.common.enums.TokenType;
import com.newsportal.news_management_system.features.auth.entity.BlacklistedTokenInfo;
import com.newsportal.news_management_system.common.dto.TokenValidationResult;
import com.newsportal.news_management_system.features.user.entity.User;
import com.newsportal.news_management_system.features.auth.repository.RefreshTokenRepository;
import com.newsportal.news_management_system.features.auth.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistManager {
    private final TokenBlacklistRepository blacklistRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public void blacklistToken(User user, TokenValidationResult validation) {
        long ttlSeconds = calculateTtlSeconds(validation.getExpiration());

        if (ttlSeconds <= 0) {
            log.warn("Token already expired, skipping blacklist: JTI={}", validation.getJti());
            return;
        }

        BlacklistedTokenInfo tokenInfo = buildTokenInfo(user, validation);

        blacklistRepository.addToBlacklist(validation.getJti(), tokenInfo, ttlSeconds);
        refreshTokenRepository.removeUserRefreshToken(user.getUserId(), validation.getJti());

        log.info("Token blacklisted successfully: JTI={}, UserId={}, TokenPurpose={}, TTL={}s",
                validation.getJti(), user.getUserId(), TokenPurpose.REFRESH, ttlSeconds);
    }

    private long calculateTtlSeconds(Date expiration) {
        return Duration.between(Instant.now(), expiration.toInstant()).getSeconds();
    }

    private BlacklistedTokenInfo buildTokenInfo(User user, TokenValidationResult validation) {
        return BlacklistedTokenInfo.builder()
                .userId(user.getUserId())
                .tokenPurpose(TokenPurpose.REFRESH)
                .blacklistedAt(Instant.now())
                .expiresAt(validation.getExpiration().toInstant())
                .build();
    }
}
