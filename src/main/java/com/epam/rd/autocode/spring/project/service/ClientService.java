package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ClientService {
    ClientDTO getByEmail(String email);
    ClientDTO updateProfile(String email, ClientDTO dto);
    void changePassword(String email, String oldPassword, String newPassword);
    ClientDTO recharge(String email, BigDecimal amount);
    List<ClientDTO> getAll();
    void toggleBlock(Long clientId);
}
