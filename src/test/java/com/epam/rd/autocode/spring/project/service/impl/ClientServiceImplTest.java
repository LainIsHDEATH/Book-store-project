package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock ClientRepository clientRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks ClientServiceImpl clientService;

    @Test
    void getClientByEmail_shouldThrow_whenNotFound() {
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> clientService.getClientByEmail("a@a"));
    }

    @Test
    void getAllClients_shouldMapDtos() {
        Client c = new Client(null, "a@a", "p", "N", new BigDecimal("1.00"));
        c.setIsBlocked(false);
        when(clientRepository.findAll()).thenReturn(List.of(c));

        List<ClientDTO> res = clientService.getAllClients();

        assertEquals(1, res.size());
        assertEquals("a@a", res.get(0).getEmail());
        assertEquals(new BigDecimal("1.00"), res.get(0).getBalance());
    }

    @Test
    void addClient_shouldThrow_whenExists() {
        ClientDTO dto = ClientDTO.builder().email("a@a").build();
        when(clientRepository.existsByEmail("a@a")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> clientService.addClient(dto));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void addClient_shouldSave_whenNotExists() {
        ClientDTO dto = ClientDTO.builder()
                .email("a@a").password("p").name("N").balance(BigDecimal.ZERO)
                .build();

        when(clientRepository.existsByEmail("a@a")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        ClientDTO res = clientService.addClient(dto);

        assertEquals("a@a", res.getEmail());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void updateClientById_shouldEncodePassword_whenProvided() {
        Client existing = new Client(1L, "a@a", "old", "Old", BigDecimal.ZERO);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("ENC");
        when(clientRepository.save(existing)).thenReturn(existing);

        ClientDTO dto = ClientDTO.builder()
                .email("b@b")
                .name("New")
                .balance(new BigDecimal("9.00"))
                .password("newpass")
                .build();

        clientService.updateClientById(1L, dto);

        assertEquals("b@b", existing.getEmail());
        assertEquals("New", existing.getName());
        assertEquals(new BigDecimal("9.00"), existing.getBalance());
        assertEquals("ENC", existing.getPassword());
        verify(clientRepository).save(existing);
    }

    @Test
    void rechargeBalanceByEmail_shouldAddAmount() {
        Client existing = new Client(1L, "a@a", "p", "N", new BigDecimal("10.00"));
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(existing));

        clientService.rechargeBalanceByEmail("a@a", new BigDecimal("2.50"));

        assertEquals(new BigDecimal("12.50"), existing.getBalance());
        verify(clientRepository).save(existing);
    }

    @Test
    void toggleBlock_shouldInvertFlag() {
        Client existing = new Client(1L, "a@a", "p", "N", BigDecimal.ZERO);
        existing.setIsBlocked(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));

        clientService.toggleBlock(1L);

        assertTrue(existing.getIsBlocked());
        verify(clientRepository).save(existing);
    }
}