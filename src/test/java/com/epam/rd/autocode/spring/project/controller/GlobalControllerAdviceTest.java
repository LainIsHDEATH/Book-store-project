package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalControllerAdviceTest {

    @Test
    void currentUser_shouldReturnNull_whenAuthNull() {
        ClientService clientService = Mockito.mock(ClientService.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(clientService);

        assertNull(advice.currentUser(null));
    }

    @Test
    void currentUser_shouldReturnNull_whenNotClientRole() {
        ClientService clientService = Mockito.mock(ClientService.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(clientService);

        var auth = new UsernamePasswordAuthenticationToken(
                "a@a", "N/A", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );

        assertNull(advice.currentUser(auth));
    }

    @Test
    void currentUser_shouldReturnBalance_whenClientRole() {
        ClientService clientService = Mockito.mock(ClientService.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(clientService);

        when(clientService.getClientByEmail("a@a"))
                .thenReturn(ClientDTO.builder().balance(new BigDecimal("12.34")).build());

        var auth = new UsernamePasswordAuthenticationToken(
                "a@a", "N/A", List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        assertEquals(new BigDecimal("12.34"), advice.currentUser(auth));
    }
}