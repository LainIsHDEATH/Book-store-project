package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = getCookieValue(request, "refresh_token");
        if (refresh != null) {
            refreshTokenService.deleteByToken(refresh);
        }

        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");

        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie c = new Cookie(name, "");
        c.setPath("/");
        c.setHttpOnly(true);
        c.setMaxAge(0);
        response.addCookie(c);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
