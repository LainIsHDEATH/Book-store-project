package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ClientDTO getByEmail(String email) {
        return toDto(clientRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Client not found")));
    }

    @Override
    public ClientDTO updateProfile(String email, ClientDTO dto) {
        Client c = clientRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Client not found"));
        c.setName(dto.getName());
        return toDto(clientRepository.save(c));
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        Client c = clientRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Client not found"));
        if (!passwordEncoder.matches(oldPassword, c.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        c.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(c);
    }

    @Override
    public ClientDTO recharge(String email, BigDecimal amount) {
        Client c = clientRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Client not found"));
        c.setBalance(c.getBalance().add(amount));
        return toDto(clientRepository.save(c));
    }

    @Override
    public List<ClientDTO> getAll() {
        return clientRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public void toggleBlock(Long clientId) {
        Client c = clientRepository.findById(clientId).orElseThrow(() -> new NotFoundException("Client not found"));
        c.setBlocked(!c.isBlocked());
        clientRepository.save(c);
    }

    private ClientDTO toDto(Client c) {
        ClientDTO dto = new ClientDTO();
        dto.setId(c.getId());
        dto.setEmail(c.getEmail());
        dto.setName(c.getName());
        dto.setBalance(c.getBalance());
        dto.setBlocked(c.isBlocked());
        return dto;
    }
}
