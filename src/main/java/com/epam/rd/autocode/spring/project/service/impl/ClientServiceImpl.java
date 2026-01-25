package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(ClientServiceImpl::toDto).toList();
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

    @Override
    public void deleteClientByEmail(String email) {
        Client c = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        clientRepository.delete(c);
    }

    @Override
    public ClientDTO addClient(ClientDTO client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new AlreadyExistException("Client already exists: " + client.getEmail());
        }
        Client saved = clientRepository.save(toEntity(client));
        return toDto(saved);
    }

    private static ClientDTO toDto(Client c) {
        return new ClientDTO(c.getEmail(), c.getPassword(), c.getName(), c.getBalance());
    }

    private static Client toEntity(ClientDTO d) {
        return new Client(null, d.getEmail(), d.getPassword(), d.getName(), d.getBalance());
    }
}
