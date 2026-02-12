package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ClientService clientService;

    @ModelAttribute("clientBalance")
    public BigDecimal currentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        boolean isClient = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_CLIENT"::equals);

        if (!isClient) {
            return null;
        }

        String email = auth.getName();
        return clientService.getClientByEmail(email).getBalance();
    }
}
