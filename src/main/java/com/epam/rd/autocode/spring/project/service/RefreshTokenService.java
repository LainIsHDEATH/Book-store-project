package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(String email);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUserEmail(String email);
}
