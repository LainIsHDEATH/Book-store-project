package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final ClientRepository clientRepository;

    @Override
    public void unlockUser(String email) {
        clientRepository.findByEmail(email).ifPresent(c -> {
            c.setBlocked(false);
            clientRepository.save(c);
        });
    }
}
