package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getMyOrders(String email);
    List<OrderDTO> getAllOrders();
    OrderDTO getById(Long id);
    OrderDTO getMyOrder(String email, Long id);
    OrderDTO checkout(String email);
    void cancelMyOrder(String email, Long orderId);
    void updateStatus(Long orderId, OrderStatus status, String employeeEmail);
}
