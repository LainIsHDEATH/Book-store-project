package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class RegistrationController {

    private final RegistrationService registration;

    public RegistrationController(RegistrationService registration) {
        this.registration = registration;
    }

    @GetMapping("/register")
    public String page(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String submit(@Valid @ModelAttribute("form") ClientRegisterRequest form,
                         BindingResult br) {
        if (br.hasErrors()) return "auth/register";

        try {
            registration.register(form);
        } catch (IllegalArgumentException e) {
            switch (e.getMessage()) {
                case "Passwords do not match" ->
                        br.rejectValue("confirmPassword", "pwd.mismatch", "Passwords do not match");
                case "Email already exists" ->
                        br.rejectValue("email", "email.exists", "Email already exists");
                case "Phone already exists" ->
                        br.rejectValue("phone", "phone.exists", "Phone already exists");
                default ->
                        br.reject("register.failed", "Registration failed");
            }
            return "auth/register";
        }

        return "redirect:/auth/login";
    }
}
