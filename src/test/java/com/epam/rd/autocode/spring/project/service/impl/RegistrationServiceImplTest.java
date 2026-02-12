package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.RegisterRequestDTO;
import com.epam.rd.autocode.spring.project.exception.PasswordMismatchException;
import com.epam.rd.autocode.spring.project.exception.UserAlreadyExistsException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock ClientRepository clients;
    @Mock EmployeeRepository employees;
    @Mock PasswordEncoder encoder;

    @InjectMocks RegistrationServiceImpl service;

    @Test
    void register_shouldThrow_whenPasswordsMismatch() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("a@a");
        req.setPassword("1");
        req.setConfirmPassword("2");

        assertThrows(PasswordMismatchException.class, () -> service.register(req));
    }

    @Test
    void register_shouldThrow_whenEmailExists() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("a@a");
        req.setPassword("1");
        req.setConfirmPassword("1");

        when(clients.existsByEmail("a@a")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.register(req));
    }

    @Test
    void register_shouldCreateClient_whenRoleClient() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("  A@A  ");
        req.setName("  Ivan ");
        req.setPassword("p");
        req.setConfirmPassword("p");
        req.setRole(Role.CLIENT);

        when(clients.existsByEmail("a@a")).thenReturn(false);
        when(employees.existsByEmail("a@a")).thenReturn(false);
        when(encoder.encode("p")).thenReturn("ENC");

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);

        service.register(req);

        verify(clients).save(captor.capture());
        Client c = captor.getValue();

        assertEquals("a@a", c.getEmail());
        assertEquals("Ivan", c.getName());
        assertEquals("ENC", c.getPassword());
        assertEquals(BigDecimal.ZERO, c.getBalance());
        assertEquals(false, c.getIsBlocked());
        verify(employees, never()).save(any());
    }

    @Test
    void register_shouldCreateEmployee_whenRoleEmployee() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("emp@mail.com");
        req.setName("Emp");
        req.setPhone("123");
        req.setBirthDate(""); // должно стать null
        req.setPassword("p");
        req.setConfirmPassword("p");
        req.setRole(Role.EMPLOYEE);

        when(clients.existsByEmail("emp@mail.com")).thenReturn(false);
        when(employees.existsByEmail("emp@mail.com")).thenReturn(false);
        when(encoder.encode("p")).thenReturn("ENC");

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);

        service.register(req);

        verify(employees).save(captor.capture());
        Employee e = captor.getValue();

        assertEquals("emp@mail.com", e.getEmail());
        assertEquals("Emp", e.getName());
        assertEquals("123", e.getPhone());
        assertNull(e.getBirthDate());
        assertEquals("ENC", e.getPassword());
        assertEquals(false, e.getIsBlocked());
        verify(clients, never()).save(any());
    }
}