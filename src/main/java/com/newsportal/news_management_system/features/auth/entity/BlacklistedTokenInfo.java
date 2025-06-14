package com.newsportal.news_management_system.features.auth.entity;

import com.newsportal.news_management_system.common.enums.TokenPurpose;
import com.newsportal.news_management_system.common.enums.TokenType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistedTokenInfo {
    private Long userId;
    private TokenPurpose tokenPurpose;
    private Instant blacklistedAt;
    private Instant expiresAt;
}
