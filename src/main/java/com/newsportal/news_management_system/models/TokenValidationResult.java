package com.newsportal.news_management_system.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationResult {
    private boolean valid;
    private String jti;
    private Long userId;
    private String username;
    private Date expiration;
    private String errorMessage;

    public static TokenValidationResult invalid(String errorMessage) {
        return TokenValidationResult.builder()
                .valid(false)
                .errorMessage(errorMessage)
                .build();
    }

    public static TokenValidationResult valid(String jti, Long userId, String username, Date expiration) {
        return TokenValidationResult.builder()
                .valid(true)
                .jti(jti)
                .userId(userId)
                .username(username)
                .expiration(expiration)
                .build();
    }
}
