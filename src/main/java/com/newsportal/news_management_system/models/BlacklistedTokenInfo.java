package com.newsportal.news_management_system.models;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistedTokenInfo {
    private Long userId;
    private String tokenType;
    private Instant blacklistedAt;
    private Instant expiresAt;
}
