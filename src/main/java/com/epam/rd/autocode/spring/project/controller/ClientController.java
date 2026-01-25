package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientDTO> all() {
        return clientService.getAllClients();
    }

    @GetMapping("/{email}")
    public ClientDTO one(@PathVariable String email) {
        return clientService.getClientByEmail(email);
    }

    @PostMapping
    public ClientDTO add(@RequestBody @Valid ClientDTO dto) {
        return clientService.addClient(dto);
    }

    @PutMapping("/{email}")
    public ClientDTO update(@PathVariable String email, @RequestBody @Valid ClientDTO dto) {
        return clientService.updateClientByEmail(email, dto);
    }

    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        clientService.deleteClientByEmail(email);
    }
}
