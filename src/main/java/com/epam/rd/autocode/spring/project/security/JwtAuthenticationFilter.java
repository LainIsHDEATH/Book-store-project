package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
            authenticateUser(accessToken, request);
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

                        authenticateUser(newAccessToken, request);
                    });
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String accessToken, HttpServletRequest request) {
        String email = jwtUtils.getUserNameFromJwtToken(accessToken); // или getEmailFromJwtToken()

        // если уже аутентифицирован — не трогаем
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // если хочешь мгновенную блокировку (желательно):
        // if (userDetails instanceof BlockAwarePrincipal p && p.isBlocked()) { throw new ... }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
