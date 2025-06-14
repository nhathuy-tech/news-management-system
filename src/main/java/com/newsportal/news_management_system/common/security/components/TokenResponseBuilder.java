package com.newsportal.news_management_system.common.security.components;

import com.newsportal.news_management_system.features.auth.dto.response.AuthenticationResponse;
import com.newsportal.news_management_system.common.security.CustomUserPrincipal;
import com.newsportal.news_management_system.features.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenResponseBuilder {
    private final JwtService jwtService;

    public AuthenticationResponse buildResponse(CustomUserPrincipal userPrincipal) {
        Map<String, Object> extraClaims = new HashMap<>();
        String accessToken = jwtService.generateAccessToken(extraClaims, userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(buildUserInfo(userPrincipal))
                .build();
    }

    private Map<String, Object> buildExtraClaims(CustomUserPrincipal userPrincipal) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userPrincipal.getUserId());
        extraClaims.put("fullName", userPrincipal.getFullName());

        List<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        extraClaims.put("authorities", authorities);

        return extraClaims;
    }

    private AuthenticationResponse.UserInfo buildUserInfo(CustomUserPrincipal userPrincipal) {
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return AuthenticationResponse.UserInfo.builder()
                .userId(userPrincipal.getUserId())
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .roles(roles)
                .isActive(userPrincipal.getIsActive())
                .build();
    }
}
