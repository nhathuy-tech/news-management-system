package com.newsportal.news_management_system.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "full_name", nullable = false)
    String fullName;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;

    @Column(name = "created_date", nullable = false)
    Instant createdDate = Instant.now();

    @Column(name = "updated_date")
    Instant updatedDate;
}
