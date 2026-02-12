package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    long deleteByUserEmail(String userEmail);

    long deleteByExpiryDateBefore(Instant now);
}