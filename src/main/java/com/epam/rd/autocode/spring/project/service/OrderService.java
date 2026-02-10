package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getMyOrders(String email);
    List<OrderDTO> getAll();
    OrderDTO getById(Long id);
    OrderDTO checkout(String email);
    void cancelMyOrder(String email, Long orderId);
    void updateStatus(Long orderId, OrderStatus status, String employeeEmail);
}
