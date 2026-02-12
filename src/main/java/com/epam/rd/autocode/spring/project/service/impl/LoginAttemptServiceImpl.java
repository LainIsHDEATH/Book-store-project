package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.security.JwtUtils;
import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public LoginResult login(String email, String password, HttpServletRequest request, HttpServletResponse response) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String subjectEmail = auth.getName();

            Cookie access = new Cookie("access_token", jwtUtils.generateTokenFromEmail(subjectEmail));
            access.setHttpOnly(true);
            access.setPath("/");
            access.setMaxAge(60 * 60);
            response.addCookie(access);

            Cookie refresh = new Cookie("refresh_token",
                    refreshTokenService.createRefreshToken(subjectEmail).getToken());
            refresh.setHttpOnly(true);
            refresh.setPath("/");
            response.addCookie(refresh);

            return LoginResult.OK;

        } catch (DisabledException | LockedException ex) {
            jwtUtils.expireCookie(request, response, "access_token");
            jwtUtils.expireCookie(request, response, "refresh_token");
            return LoginResult.BLOCKED;

        } catch (AuthenticationException ex) {
            return LoginResult.INVALID;
        }
    }

    public enum LoginResult {
        OK,
        BLOCKED,
        INVALID
    }
}