package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.RegisterRequestDTO;

public interface RegistrationService {
    void register(RegisterRequestDTO request);
}
