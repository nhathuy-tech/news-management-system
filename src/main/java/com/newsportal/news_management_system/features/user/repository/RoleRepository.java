package com.newsportal.news_management_system.features.user.repository;

import com.newsportal.news_management_system.features.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByRoleName(String roleName);
    Optional<Role> findByRoleName(String roleName);
}
