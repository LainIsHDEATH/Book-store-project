package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.RegisterRequestDTO;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final ClientRepository clients;
    private final EmployeeRepository employees;
    private final PasswordEncoder encoder;

    @Override
    public void register(RegisterRequestDTO req) {
        String email = req.getEmail().trim().toLowerCase();
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (clients.existsByEmail(email) || employees.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (req.getRole() == Role.CUSTOMER) {
            Client c = new Client();
            c.setEmail(email);
            c.setName(req.getName().trim());
            c.setPassword(encoder.encode(req.getPassword()));
            c.setBalance(BigDecimal.ZERO);
            clients.save(c);
            return;
        }

        Employee e = new Employee();
        e.setEmail(email);
        e.setName(req.getName().trim());
        e.setPhone(req.getPhone());
        e.setBirthDate(req.getBirthDate() == null || req.getBirthDate().isBlank() ? null : LocalDate.parse(req.getBirthDate()));
        e.setPassword(encoder.encode(req.getPassword()));
        employees.save(e);
    }
}
