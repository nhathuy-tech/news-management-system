package com.newsportal.news_management_system.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TokenPurpose {
    ACCESS("access_token"),
    REFRESH("refresh_token");

    String value;
}
