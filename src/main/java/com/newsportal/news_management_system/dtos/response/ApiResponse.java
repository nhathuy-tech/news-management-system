package com.newsportal.news_management_system.dtos.response;

public record ApiResponse<T>(
    int code,
    String message,
    T data
) {}
