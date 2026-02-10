package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String view(Model model) {
        model.addAttribute("cart", cartService.getState());
        return "cart/view";
    }

    @GetMapping("/add/{bookId}")
    public String add(@PathVariable Long bookId) {
        cartService.add(bookId);
        return "redirect:/";
    }

    @GetMapping("/minus/{bookId}")
    public String minus(@PathVariable Long bookId) {
        cartService.minus(bookId);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{bookId}")
    public String remove(@PathVariable Long bookId) {
        cartService.remove(bookId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clear() {
        cartService.clear();
        return "redirect:/cart";
    }
}
