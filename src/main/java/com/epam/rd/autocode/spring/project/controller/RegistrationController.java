package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.RegisterRequestDTO;
import com.epam.rd.autocode.spring.project.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("request", new RegisterRequestDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterRequestDTO request, BindingResult br) {
        if (br.hasErrors()) return "auth/register";
        registrationService.register(request);
        return "redirect:/auth/login?registered=true";
    }
}
