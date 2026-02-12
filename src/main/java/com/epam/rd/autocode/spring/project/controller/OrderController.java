package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
@PreAuthorize("hasRole('CLIENT')")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    public String myOrders(Authentication auth, Model model) {
        String email = auth.getName();
        model.addAttribute("orders", orderService.getMyOrders(email));
        return "orders/my";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(Authentication auth, @PathVariable Long id) {
        String email = auth.getName();
        orderService.cancelMyOrder(email, id);
        return "redirect:/orders/my";
    }

    @GetMapping("/{id}")
    public String viewOrder(Authentication auth, @PathVariable Long id, Model model) {
        String email = auth.getName();
        model.addAttribute("order", orderService.getMyOrder(email, id)); // ownership check
        return "orders/order-details";
    }
}