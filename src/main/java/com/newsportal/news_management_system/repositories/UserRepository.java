package com.newsportal.news_management_system.repositories;

import com.newsportal.news_management_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
