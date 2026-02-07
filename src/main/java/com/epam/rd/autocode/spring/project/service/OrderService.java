package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.auth.OrderViewDTO;

import java.util.List;

public interface OrderService {

    List<OrderDTO> getOrdersByClient(String clientEmail);

    List<OrderDTO> getOrdersByEmployee(String employeeEmail);

    OrderDTO addOrder(OrderDTO order);

    List<OrderViewDTO> getOrdersForUser(String email, boolean employee);

    void cancelOrder(Long orderId, String actorEmail, boolean employee);
}
