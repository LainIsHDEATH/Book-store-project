package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/clients")
@PreAuthorize("hasAuthority('CUSTOMER')")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final PasswordResetService passwordResetService;

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        ClientDTO client =
                clientService.getClientByEmail(principal.getName());

        model.addAttribute("client", client);
        return "client/profile-form";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@Valid @ModelAttribute("client") ClientDTO dto,
                              BindingResult bindingResult,
                              Principal principal,
                              Model model) {

        if (bindingResult.hasErrors()) {
            return "client/profile-form";
        }

        clientService.updateProfile(principal.getName(), dto);
        return "redirect:/clients/profile?success=true";
    }

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
