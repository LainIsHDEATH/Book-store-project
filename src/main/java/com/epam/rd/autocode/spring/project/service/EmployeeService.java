package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;

public interface EmployeeService {
    EmployeeDTO getByEmail(String email);
    EmployeeDTO updateProfile(String email, EmployeeDTO dto);
    void changePassword(String email, String oldPassword, String newPassword);
}
