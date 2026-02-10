package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ClientService clientService;

    @Override
    public void toggleBlockClient(Long id) {
        clientService.toggleBlock(id);
    }
}
