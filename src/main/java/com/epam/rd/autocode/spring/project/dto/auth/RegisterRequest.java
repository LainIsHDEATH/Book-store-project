package com.epam.rd.autocode.spring.project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@Email String email, @NotBlank String password, @NotBlank String name, @NotBlank String role) {}
