package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.PasswordChangeDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequestMapping("/clients")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("client", clientService.getByEmail(principal.getName()));
        model.addAttribute("passwordChange", new PasswordChangeDTO());
        return "client/profile-form";
    }

    @PostMapping("/profile/save")
    public String save(@Valid @ModelAttribute("client") ClientDTO dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) return "client/profile-form";
        clientService.updateProfile(principal.getName(), dto);
        return "redirect:/clients/profile?success=true";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordChange") PasswordChangeDTO dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) return "redirect:/clients/profile?pwdError=true";
        clientService.changePassword(principal.getName(), dto.getOldPassword(), dto.getNewPassword());
        return "redirect:/clients/profile?pwdSuccess=true";
    }

    @GetMapping("/recharge")
    public String rechargePage(Principal principal, Model model) {
        model.addAttribute("client", clientService.getByEmail(principal.getName()));
        return "recharge";
    }

    @PostMapping("/recharge")
    public String recharge(Principal principal, @RequestParam BigDecimal amount) {
        clientService.recharge(principal.getName(), amount);
        return "redirect:/clients/recharge?success=true";
    }
}
