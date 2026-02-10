package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String myOrders(Principal principal, Model model) {
        model.addAttribute("orders", orderService.getMyOrders(principal.getName()));
        return "orders/my";
    }

    @GetMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String checkout(Principal principal) {
        orderService.checkout(principal.getName());
        return "redirect:/orders/my";
    }

    @GetMapping("/cancel/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String cancel(Principal principal, @PathVariable Long id) {
        orderService.cancelMyOrder(principal.getName(), id);
        return "redirect:/orders/my";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("isAuthenticated()")
    public String viewOrder(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getById(id));
        return "order-details";
    }
}
