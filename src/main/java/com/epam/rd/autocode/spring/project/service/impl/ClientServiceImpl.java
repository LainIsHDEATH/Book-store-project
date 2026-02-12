package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(ClientServiceImpl::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found: " + id));
        return toDto(c);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByEmail(String email) {
        Client c = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        return toDto(c);
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO client) {
        Client c = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        c.setEmail(client.getEmail());
        c.setPassword(client.getPassword());
        c.setName(client.getName());
        c.setBalance(client.getBalance());

        return toDto(clientRepository.save(c));
    }

    public void updateClientById(Long id, ClientDTO dto){
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found: " + id));

        client.setName(dto.getName());
        client.setBalance(dto.getBalance());

        client.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void deleteProfile(String email) {
        Client c = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        clientRepository.delete(c);
    }

    @Override
    @Transactional
    public void deleteClientById(Long id){
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found: " + id));
        clientRepository.delete(c);
    }

    @Override
    @Transactional
    public ClientDTO addClient(ClientDTO client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new AlreadyExistException("Client already exists: " + client.getEmail());
        }
        Client saved = clientRepository.save(toEntity(client));
        return toDto(saved);
    }

    @Override
    @Transactional
    public void updateProfile(String email, ClientDTO dto){
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        client.setName(dto.getName());
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void updateProfileName(String email, String name){
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        client.setName(name);
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void rechargeBalanceByEmail(String email, BigDecimal amount){
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        client.setBalance(client.getBalance().add(amount));
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void toggleBlock(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found: " + id));
        client.setIsBlocked(!Boolean.TRUE.equals(client.getIsBlocked()));
        clientRepository.save(client);
    }

    private static ClientDTO toDto(Client c) {
        return ClientDTO.builder()
                .id(c.getId())
                .email(c.getEmail())
                .name(c.getName())
                .balance(c.getBalance())
                .isBlocked(c.getIsBlocked())
                .build();
    }

    private static Client toEntity(ClientDTO d) {
        return new Client(null, d.getEmail(), d.getPassword(), d.getName(), d.getBalance());
    }
}
