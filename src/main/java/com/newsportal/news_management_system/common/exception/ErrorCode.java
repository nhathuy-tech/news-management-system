package com.newsportal.news_management_system.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    USER_EXISTED(404, "User existed", HttpStatus.BAD_REQUEST),
    USER_INACTIVE(403, "User is inactive", HttpStatus.FORBIDDEN),
    ROLE_NOT_EXISTED(404, "Role not existed", HttpStatus.NOT_FOUND),
    TOKEN_INVALID(401, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(401, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLISTED(403, "Token is blacklisted", HttpStatus.FORBIDDEN),
    TOKEN_MALFORMED(401, "Token is malformed", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND(401, "Token not found", HttpStatus.UNAUTHORIZED),
    TOKEN_INACTIVE(403, "User account is inactive", HttpStatus.FORBIDDEN),
    USER_NOT_AUTHORIZED(403, "User not authorized", HttpStatus.FORBIDDEN);

    int code;
    String message;
    HttpStatus status;
}
