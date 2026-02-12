package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.exception.JwtAuthenticationException;
import com.epam.rd.autocode.spring.project.model.UserPrincipal;
import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = getCookieValue(request, "access_token");
        String refreshToken = getCookieValue(request, "refresh_token");

        if (accessToken != null && jwtUtils.validateJwtToken(accessToken)) {
            authenticateUser(accessToken, refreshToken, request, response);
        }
        else if (refreshToken != null) {
            refreshTokenService.findByToken(refreshToken)
                    .map(token -> {
                        try {return refreshTokenService.verifyExpiration(token); }
                        catch (Exception e) {return null; }
                    })
                    .ifPresent(token -> {
                        String email = token.getUserEmail();
                        String newAccessToken = jwtUtils.generateTokenFromEmail(email);

                        Cookie cookie = new Cookie("access_token", newAccessToken);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(60 * 60);
                        response.addCookie(cookie);

                        try {
                            authenticateUser(newAccessToken, refreshToken, request, response);
                        } catch (IOException e) {
                            throw new JwtAuthenticationException("Error while authenticating user");
                        }
                    });
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String accessToken, String refreshToken, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = jwtUtils.getUserNameFromJwtToken(accessToken);
        if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) return;

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (isBlocked(userDetails)) {
            handleBlocked(request, response, email, refreshToken);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isBlocked(UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal p) {
            return p.isBlocked();
        }
        return !userDetails.isAccountNonLocked() || !userDetails.isEnabled();
    }

    private void handleBlocked(HttpServletRequest request,
                               HttpServletResponse response,
                               String email,
                               String refreshToken) throws IOException {

        SecurityContextHolder.clearContext();

        refreshTokenService.deleteByUserEmail(email);

        if (refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        clearAuthCookies(request, response);

        response.sendRedirect(request.getContextPath() + "/blocked");
    }

    private void clearAuthCookies(HttpServletRequest request, HttpServletResponse response) {
        jwtUtils.expireCookie(request, response, "access_token");
        jwtUtils.expireCookie(request, response, "refresh_token");
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
