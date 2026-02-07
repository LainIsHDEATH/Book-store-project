package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.auth.AuthResponse;
import com.epam.rd.autocode.spring.project.dto.auth.LoginRequest;
import com.epam.rd.autocode.spring.project.dto.auth.RegisterRequest;
import com.epam.rd.autocode.spring.project.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
        AuthResponse auth = authService.register(request);
        response.addHeader("Set-Cookie", jwtCookie(auth.token()).toString());
        return auth;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        response.addHeader("Set-Cookie", jwtCookie(auth.token()).toString());
        return auth;
    }

    private ResponseCookie jwtCookie(String token) {
        return ResponseCookie.from("JWT", token).path("/").httpOnly(true).build();
    }
}
