package com.epam.rd.autocode.spring.project.service.auth;

import com.epam.rd.autocode.spring.project.dto.auth.AuthResponse;
import com.epam.rd.autocode.spring.project.dto.auth.LoginRequest;
import com.epam.rd.autocode.spring.project.dto.auth.RegisterRequest;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.auth.AuthUser;
import com.epam.rd.autocode.spring.project.model.auth.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.auth.AuthUserRepository;
import com.epam.rd.autocode.spring.project.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthUserRepository authUserRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        authUserRepository.findByEmail(request.email()).ifPresent(x -> { throw new AlreadyExistException("Email already registered"); });
        Role role = "employee".equalsIgnoreCase(request.role()) ? Role.ROLE_EMPLOYEE : Role.ROLE_CUSTOMER;
        String encoded = passwordEncoder.encode(request.password());
        authUserRepository.save(new AuthUser(null, request.email(), encoded, role, false));

        if (role == Role.ROLE_EMPLOYEE) {
            employeeRepository.save(new Employee(null, request.email(), encoded, request.name(), LocalDate.now().minusYears(20), "+380000000"));
        } else {
            clientRepository.save(new Client(null, request.email(), encoded, request.name(), BigDecimal.valueOf(1000)));
        }
        log.info("Registered: {} with role {}", request.email(), role);
        return login(new LoginRequest(request.email(), request.password()));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var principal = org.springframework.security.core.userdetails.User.withUsername(request.email()).password("").authorities("USER").build();
        return new AuthResponse(jwtService.generateToken(principal));
    }

    @Override
    public void blockUser(String email, boolean blocked) {
        var user = authUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        user.setBlocked(blocked);
        authUserRepository.save(user);
        log.warn("User {} blocked status changed to {}", email, blocked);
    }
}
