package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ClientService {

    List<ClientDTO> getAllClients();

    ClientDTO getClientById(Long id);

    ClientDTO getClientByEmail(String email);

    ClientDTO updateClientByEmail(String email, ClientDTO client);
    void updateClientById(Long id, ClientDTO client);

    void deleteProfile(String email);

    void deleteClientById(Long id);

    ClientDTO addClient(ClientDTO client);

    void updateProfile(String email, ClientDTO client);

    void rechargeBalanceByEmail(String email, BigDecimal amount);

    void updateProfileName(String email, String name);

    void toggleBlock(Long id);
}
