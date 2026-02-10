package com.epam.rd.autocode.spring.project.service;

public interface PasswordResetService {
    void sendResetLink(String email);
}
