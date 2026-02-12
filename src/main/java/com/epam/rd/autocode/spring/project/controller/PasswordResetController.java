package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot")
    public String forgotPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot")
    public String forgotSubmit(@RequestParam String email) {
        passwordResetService.requestReset(email);
        return "redirect:/auth/password/forgot?sent=true";
    }

    @GetMapping("/reset")
    public String resetPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset")
    public String resetSubmit(@RequestParam String token,
                              @RequestParam String password,
                              @RequestParam String confirm) {
        if (!password.equals(confirm)) {
            return "redirect:/auth/password/reset?token=" + token + "&mismatch=true";
        }
        passwordResetService.resetPassword(token, password);
        return "redirect:/auth/login?reset=true";
    }
}
