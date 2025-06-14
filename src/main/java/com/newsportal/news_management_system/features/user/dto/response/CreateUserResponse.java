package com.newsportal.news_management_system.features.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserResponse {
    Long userId;
    String email;
    String fullName;
    List<String> roleNames; // Changed from single roleName to list
    Boolean isActive;
    Instant createdDate;
}
