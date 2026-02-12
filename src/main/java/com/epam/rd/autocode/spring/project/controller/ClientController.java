package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.RechargeFormDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientProfileDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/clients")
@PreAuthorize("hasRole('CLIENT')")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/profile")
    public String showProfile(Authentication auth, Model model) {
        ClientDTO client =
                clientService.getClientByEmail(auth.getName());

        UpdateClientProfileDTO form = new UpdateClientProfileDTO();
        form.setName(client.getName());

        model.addAttribute("client", client);
        model.addAttribute("form", form);

        return "client/profile-form";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@Valid @ModelAttribute("form") UpdateClientProfileDTO form,
                              BindingResult bindingResult,
                              Authentication auth,
                              Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("client", clientService.getClientByEmail(auth.getName()));
            return "client/profile-form";
        }

        String email = auth.getName();
        clientService.updateProfileName(email, form.getName());
        return "redirect:/clients/profile?success=true";
    }

    @GetMapping("/recharge")
    public String page(Authentication auth,
                       Model model) {
        ClientDTO client =
                clientService.getClientByEmail(auth.getName());

        model.addAttribute("client", client);

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RechargeFormDTO());
        }
        return "client/recharge";
    }

    @PostMapping("/recharge")
    public String recharge(@Valid @ModelAttribute("form") RechargeFormDTO dto,
                           BindingResult br,
                           Authentication auth,
                           Model model) {
        String email = auth.getName();
        ClientDTO client = clientService.getClientByEmail(email);
        model.addAttribute("client", client);

        if (br.hasErrors()) {
            return "client/recharge";
        }

        clientService.rechargeBalanceByEmail(email, dto.getAmount());
        return "redirect:/clients/recharge?success=true";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication auth, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String email = auth.getName();

        clientService.deleteProfile(email);

        String refresh = getCookieValue(request, "refresh_token");
        if (refresh != null) {
            refreshTokenService.deleteByToken(refresh);
        }

        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");

        SecurityContextHolder.clearContext();

        return "redirect:/login?deleted=true";
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie c = new Cookie(name, "");
        c.setPath("/");
        c.setHttpOnly(true);
        c.setMaxAge(0);
        response.addCookie(c);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
