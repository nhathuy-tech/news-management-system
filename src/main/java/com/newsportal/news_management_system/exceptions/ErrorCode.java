package com.newsportal.news_management_system.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    USER_EXISTED(404, "User existed", HttpStatus.BAD_REQUEST),
    USER_INACTIVE(403, "User is inactive", HttpStatus.FORBIDDEN),
    ROLE_NOT_EXISTED(404, "Role not existed", HttpStatus.NOT_FOUND),
    TOKEN_INVALID(401, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLIST_FAILED(500, "Failed to blacklist token", HttpStatus.INTERNAL_SERVER_ERROR)

    ;
    int code;
    String message;
    HttpStatusCode status;

}
