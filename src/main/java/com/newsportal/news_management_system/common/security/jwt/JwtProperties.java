package com.newsportal.news_management_system.common.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
public class JwtProperties {
    private String secret = "mySecretKey";
    private long accessTokenExpiration = 3600;
    private long refreshTokenExpiration = 604800;
}
