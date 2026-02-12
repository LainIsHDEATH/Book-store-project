package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.security.JwtUtils;
import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtils jwtUtils;
    @Mock RefreshTokenService refreshTokenService;

    @InjectMocks LoginAttemptServiceImpl service;

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;

    @Test
    void login_shouldSetCookies_andReturnOK() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("me@mail.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(jwtUtils.generateTokenFromEmail("me@mail.com")).thenReturn("ACCESS");
        var refresh = com.epam.rd.autocode.spring.project.model.RefreshToken.builder()
                .userEmail("me@mail.com")
                .token("REFRESH")
                .expiryDate(Instant.now().plusSeconds(60))
                .build();
        when(refreshTokenService.createRefreshToken("me@mail.com")).thenReturn(refresh);

        var result = service.login("me@mail.com", "pass", request, response);

        assertEquals(LoginAttemptServiceImpl.LoginResult.OK, result);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieCaptor.capture());

        assertEquals("access_token", cookieCaptor.getAllValues().get(0).getName());
        assertEquals("refresh_token", cookieCaptor.getAllValues().get(1).getName());
        verify(jwtUtils, never()).expireCookie(any(), any(), anyString());
    }

    @Test
    void login_shouldExpireCookies_andReturnBLOCKED_whenLocked() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new LockedException("locked"));

        var result = service.login("a@a", "p", request, response);

        assertEquals(LoginAttemptServiceImpl.LoginResult.BLOCKED, result);
        verify(jwtUtils).expireCookie(request, response, "access_token");
        verify(jwtUtils).expireCookie(request, response, "refresh_token");
    }

    @Test
    void login_shouldReturnINVALID_onAuthException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(mock(AuthenticationException.class));

        var result = service.login("a@a", "p", request, response);

        assertEquals(LoginAttemptServiceImpl.LoginResult.INVALID, result);
        verify(jwtUtils, never()).expireCookie(any(), any(), anyString());
        verify(response, never()).addCookie(any());
    }
}