package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public EmployeeDTO getByEmail(String email) {
        return toDto(employeeRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Employee not found")));
    }

    @Override
    public EmployeeDTO updateProfile(String email, EmployeeDTO dto) {
        Employee e = employeeRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Employee not found"));
        e.setName(dto.getName());
        e.setPhone(dto.getPhone());
        e.setBirthDate(dto.getBirthDate());
        return toDto(employeeRepository.save(e));
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        Employee e = employeeRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Employee not found"));
        if (!passwordEncoder.matches(oldPassword, e.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        e.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(e);
    }

    private EmployeeDTO toDto(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(e.getId());
        dto.setEmail(e.getEmail());
        dto.setName(e.getName());
        dto.setPhone(e.getPhone());
        dto.setBirthDate(e.getBirthDate());
        dto.setBlocked(e.isBlocked());
        return dto;
    }
}
