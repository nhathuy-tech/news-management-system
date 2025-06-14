package com.newsportal.news_management_system.common.security.components;

import com.newsportal.news_management_system.common.exception.AppException;
import com.newsportal.news_management_system.common.exception.ErrorCode;
import com.newsportal.news_management_system.common.dto.TokenValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenValidationHandler {
    private final TokenValidator tokenValidator;

    public TokenValidationResult validateAndThrow(String token) {
        TokenValidationResult validation = tokenValidator.validate(token);

        if (!validation.isValid()) {
            log.warn("Token validation failed: {}", validation.getErrorMessage());
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        return validation;
    }
}
