package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.model.UserPrincipal;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByEmail(username)
                .map(c -> (UserDetails) new UserPrincipal(c.getEmail(), c.getPassword(), c.isBlocked(), "ROLE_CLIENT"))
                .or(() -> employeeRepository.findByEmail(username)
                        .map(e -> (UserDetails) new UserPrincipal(e.getEmail(), e.getPassword(), e.isBlocked(), "ROLE_EMPLOYEE")))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}