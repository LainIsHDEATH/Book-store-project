package com.epam.rd.autocode.spring.project.service;

public interface PasswordResetService {
    void requestReset(String email);
    void resetPassword(String token, String newPassword);
}
