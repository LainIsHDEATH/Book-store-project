package com.epam.rd.autocode.spring.project.service.auth;

import com.epam.rd.autocode.spring.project.dto.auth.AuthResponse;
import com.epam.rd.autocode.spring.project.dto.auth.LoginRequest;
import com.epam.rd.autocode.spring.project.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void blockUser(String email, boolean blocked);
}
