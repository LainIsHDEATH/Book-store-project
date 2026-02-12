package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeProfileDTO;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeByEmail(String email);

    EmployeeDTO updateProfile(String email, UpdateEmployeeProfileDTO employee);

    void deleteProfile(String email);

    EmployeeDTO addEmployee(EmployeeDTO employee);
}
