package com.epam.rd.autocode.spring.project.repo.auth;

import com.epam.rd.autocode.spring.project.model.auth.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);
}
