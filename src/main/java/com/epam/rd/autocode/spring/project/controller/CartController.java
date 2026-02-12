package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.exception.InvalidBalanceException;
import com.epam.rd.autocode.spring.project.exception.LimitException;
import com.epam.rd.autocode.spring.project.service.CartService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
@PreAuthorize("hasRole('CLIENT')")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping
    public String view(Authentication auth, Model model) {
        String email = auth.getName();
        model.addAttribute("cart", cartService.getState(email));
        return "cart/view";
    }

    @PostMapping("/add/{bookId}")
    public String add(Authentication auth, @PathVariable Long bookId, HttpServletRequest req) {
        String email = auth.getName();
        try {
            cartService.add(email, bookId);
            return "redirect:" + safeBackUrl(req, "/");
        } catch (LimitException e) {
            return "redirect:" + safeBackUrlWithParams(req, "/", "stockErrBookId", String.valueOf(bookId));
        }

    }

    @PostMapping("/minus/{bookId}")
    public String minus(Authentication auth, @PathVariable Long bookId, HttpServletRequest req) {
        String email = auth.getName();
        cartService.minus(email, bookId);
        return "redirect:" + safeBackUrl(req, "/");
    }

    @PostMapping("/remove/{bookItemId}")
    public String remove(@PathVariable Long bookItemId) {
        cartService.remove(bookItemId);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Authentication auth, Model model) {
        String email = auth.getName();
        try {
            orderService.checkout(email);
            return "redirect:/orders/my?success=true";
        } catch (InvalidBalanceException ex) {
            model.addAttribute("cart", cartService.getState(email));
            model.addAttribute("errorKey", "cart.error.insufficientBalance");
            return "cart/view";
        } catch (Exception e) {
            model.addAttribute("cart", cartService.getState(email));
            model.addAttribute("errorKey", "cart.error.generic");
            return "redirect:/cart";
        }
    }

    @PostMapping("/clear")
    public String clear(Authentication auth) {
        String email = auth.getName();
        cartService.clear(email);
        return "redirect:/cart";
    }

    private String safeBackUrl(HttpServletRequest req, String fallback) {
        String ref = req.getHeader("Referer");
        if (ref == null || ref.isBlank()) return fallback;

        String host = req.getHeader("Host");
        if (host != null && ref.contains("://" + host + "/")) return ref;

        return fallback;
    }

    private String safeBackUrlWithParams(HttpServletRequest req, String fallback, String key, String value) {
        String ref = req.getHeader("Referer");
        if (ref == null || ref.isBlank()) {
            return appendParam(fallback, key, value);
        }

        try {
            var uri = java.net.URI.create(ref);

            String host = uri.getHost();
            if (host != null && !host.equalsIgnoreCase(req.getServerName())) {
                return appendParam(fallback, key, value);
            }

            String path = (uri.getPath() == null) ? "/" : uri.getPath();
            String query = uri.getQuery();
            String base = path + (query == null || query.isBlank() ? "" : "?" + query);

            return appendParam(base, key, value);
        } catch (Exception ex) {
            return appendParam(fallback, key, value);
        }
    }

    private String appendParam(String url, String key, String value) {
        String glue = url.contains("?") ? "&" : "?";
        return url + glue
                + java.net.URLEncoder.encode(key, java.nio.charset.StandardCharsets.UTF_8)
                + "="
                + java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}