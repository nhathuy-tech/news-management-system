package com.newsportal.news_management_system.common.security.components;

import com.newsportal.news_management_system.common.exception.AppException;
import com.newsportal.news_management_system.common.exception.ErrorCode;
import com.newsportal.news_management_system.features.user.entity.User;
import com.newsportal.news_management_system.features.user.repository.UserRepository;
import com.newsportal.news_management_system.features.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResolver {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User resolveUserFromToken(String token) {
        String email = jwtService.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}

