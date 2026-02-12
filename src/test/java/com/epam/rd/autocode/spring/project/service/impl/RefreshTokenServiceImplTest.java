package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.exception.TokenExpirationException;
import com.epam.rd.autocode.spring.project.model.RefreshToken;
import com.epam.rd.autocode.spring.project.repo.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks RefreshTokenServiceImpl service;

    @BeforeEach
    void setDuration() throws Exception {
        Field f = RefreshTokenServiceImpl.class.getDeclaredField("refreshTokenDurationMs");
        f.setAccessible(true);
        f.set(service, 60_000L);
    }

    @Test
    void findByToken_shouldDelegateToRepo() {
        RefreshToken t = RefreshToken.builder().token("t").build();
        when(refreshTokenRepository.findByToken("t")).thenReturn(Optional.of(t));

        Optional<RefreshToken> res = service.findByToken("t");

        assertTrue(res.isPresent());
        verify(refreshTokenRepository).findByToken("t");
    }

    @Test
    void createRefreshToken_shouldDeleteOld_andSaveNew() {
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken rt = service.createRefreshToken("a@a");

        assertEquals("a@a", rt.getUserEmail());
        assertNotNull(rt.getToken());
        assertTrue(rt.getExpiryDate().isAfter(Instant.now()));

        verify(refreshTokenRepository).deleteByUserEmail("a@a");
        verify(refreshTokenRepository).flush();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_shouldThrow_andDelete_whenExpired() {
        RefreshToken expired = RefreshToken.builder()
                .token("t")
                .expiryDate(Instant.now().minusSeconds(1))
                .build();

        assertThrows(TokenExpirationException.class, () -> service.verifyExpiration(expired));
        verify(refreshTokenRepository).delete(expired);
    }

    @Test
    void verifyExpiration_shouldReturnToken_whenValid() {
        RefreshToken valid = RefreshToken.builder()
                .token("t")
                .expiryDate(Instant.now().plusSeconds(60))
                .build();

        RefreshToken res = service.verifyExpiration(valid);

        assertSame(valid, res);
        verify(refreshTokenRepository, never()).delete(any());
    }
}