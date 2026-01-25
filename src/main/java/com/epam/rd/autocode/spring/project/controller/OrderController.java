package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/client/{email}")
    public List<OrderDTO> byClient(@PathVariable String email) {
        return orderService.getOrdersByClient(email);
    }

    @GetMapping("/employee/{email}")
    public List<OrderDTO> byEmployee(@PathVariable String email) {
        return orderService.getOrdersByEmployee(email);
    }

    @PostMapping
    public OrderDTO add(@RequestBody @Valid OrderDTO dto) {
        return orderService.addOrder(dto);
    }
}
