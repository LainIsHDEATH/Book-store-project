package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.AdminRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.RegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final ClientRepository clients;
    private final EmployeeRepository employees;
    private final AdminRepository admins;
    private final PasswordEncoder encoder;

    public RegistrationService(ClientRepository clients,
                               EmployeeRepository employees,
                               AdminRepository admins,
                               PasswordEncoder encoder) {
        this.clients = clients;
        this.employees = employees;
        this.admins = admins;
        this.encoder = encoder;
    }

    @Transactional
    public void register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        String phone = req.getPhone().trim();

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (emailExistsAnywhere(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (phoneExistsAnywhere(phone)) {
            throw new IllegalArgumentException("Phone already exists");
        }

        switch (req.getRole()) {
            case CLIENT -> registerClient(req, email, phone);
            case EMPLOYEE -> registerEmployee(req, email, phone);
            case ADMIN -> registerAdmin(req, email, phone);
        }
    }

    private void registerClient(RegisterRequest req, String email, String phone) {
        Client c = new Client();
        c.setEmail(email);
        c.setName(req.getName().trim());
        c.setPhone(phone);
        c.setPassword(encoder.encode(req.getPassword()));
        c.setBalance(BigDecimal.ZERO);
        c.setIsBlocked(false);
        clients.save(c);
    }

    private void registerEmployee(RegisterRequest req, String email, String phone) {
        Employee e = new Employee();
        e.setEmail(email);
        e.setName(req.getName().trim());
        e.setPhone(phone);
        e.setPassword(encoder.encode(req.getPassword()));
        e.setIsBlocked(false);
        // e.setBirthDate(req.getBirthDate()); // если добавишь поле
        employees.save(e);
    }

    private void registerAdmin(RegisterRequest req, String email, String phone) {
        Admin a = new Admin();
        a.setEmail(email);
        a.setName(req.getName().trim());
        a.setPhone(phone);
        a.setPassword(encoder.encode(req.getPassword()));
        a.setIsBlocked(false);
        admins.save(a);
    }

    private boolean emailExistsAnywhere(String email) {
        return clients.existsByEmailIgnoreCase(email)
                || employees.existsByEmailIgnoreCase(email)
                || admins.existsByEmailIgnoreCase(email);
    }

    private boolean phoneExistsAnywhere(String phone) {
        return clients.existsByPhone(phone)
                || employees.existsByPhone(phone)
                || admins.existsByPhone(phone);
    }
}