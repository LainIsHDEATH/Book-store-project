package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock EmployeeRepository employeeRepository;

    @InjectMocks CustomUserDetailsService service;

    @Test
    void loadUserByUsername_shouldReturnClientPrincipal() {
        Client c = new Client(1L, "a@a", "p", "N", null);
        c.setIsBlocked(false);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        UserDetails ud = service.loadUserByUsername("a@a");

        assertEquals("a@a", ud.getUsername());
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    void loadUserByUsername_shouldReturnEmployeePrincipal_whenNoClient() {
        when(clientRepository.findByEmail("e@e")).thenReturn(Optional.empty());

        Employee e = new Employee();
        e.setEmail("e@e");
        e.setPassword("p");
        e.setIsBlocked(false);
        when(employeeRepository.findByEmail("e@e")).thenReturn(Optional.of(e));

        UserDetails ud = service.loadUserByUsername("e@e");

        assertEquals("e@e", ud.getUsername());
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
    }

    @Test
    void loadUserByUsername_shouldThrow_whenNotFound() {
        when(clientRepository.findByEmail("x")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("x")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("x"));
    }
}