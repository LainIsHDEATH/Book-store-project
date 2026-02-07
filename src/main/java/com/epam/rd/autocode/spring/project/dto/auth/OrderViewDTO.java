package com.epam.rd.autocode.spring.project.dto.auth;

import com.epam.rd.autocode.spring.project.model.auth.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderViewDTO(Long id, String clientEmail, String employeeEmail, LocalDateTime orderDate, BigDecimal price, OrderStatus status) {}
