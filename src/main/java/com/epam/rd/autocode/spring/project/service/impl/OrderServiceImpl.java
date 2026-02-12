package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.InvalidBalanceException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getMyOrders(String email){
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        return orderRepository.findByClientId(client.getId()).stream()
                .filter(o -> o.getStatus() != OrderStatus.CART)
                .map(OrderServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.CART)
                .map(OrderServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        return toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getMyOrder(String email, Long orderId) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!order.getClient().getId().equals(client.getId())) {
            throw new NotFoundException("Order not found: " + orderId);
        }

        return toDto(order);
    }

    @Override
    @Transactional
    public OrderDTO checkout(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        Order order = orderRepository.findByClientIdAndStatus(client.getId(), OrderStatus.CART)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + email));

        BigDecimal total = order.getPrice();
        if (client.getBalance().compareTo(total) < 0) {
            throw new InvalidBalanceException("Client does not have enough balance: " + email);
        }
        client.decrementBalance(total);
        order.setStatus(OrderStatus.PAID);
        order.setOrderDate(Instant.now());
        orderRepository.save(order);
        clientRepository.save(client);

        return OrderDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .clientEmail(email)
                .employeeEmail(null)
                .orderDate(LocalDateTime.ofInstant(order.getOrderDate(), ZoneId.systemDefault()))
                .price(order.getPrice())
                .bookItems(order.getItems().stream()
                        .map(it -> BookItemDTO.builder()
                                .bookNameEn(it.getBook().getNameEn())
                                .bookNameUk(it.getBook().getNameUk())
                                .bookItemId(it.getId())
                                .bookId(it.getBook().getId())
                                .orderId(it.getOrder().getId())
                                .quantity(it.getQuantity())
                                .unitPrice(it.getUnitPrice())
                                .build()
                        ).toList())
                .build();
    }

    @Override
    @Transactional
    public void cancelMyOrder(String email, Long orderId){
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!order.getClient().getId().equals(client.getId())) {
            throw new NotFoundException("Order not found: " + orderId);
        }

        if (order.getStatus() == OrderStatus.CANCELED) return;

        order.setStatus(OrderStatus.CANCELED);
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus status, String employeeEmail){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                        .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeEmail));

        order.setStatus(status);
        order.setEmployee(employee);
        orderRepository.save(order);
    }


    private static OrderDTO toDto(Order order) {
        String clientEmail = order.getClient() != null
                ? order.getClient().getEmail()
                : null;

        String employeeEmail = order.getEmployee() != null
                ? order.getEmployee().getEmail()
                : null;

        List<BookItemDTO> items = order.getItems() == null
                ? List.of()
                : order.getItems().stream()
                .map(it -> BookItemDTO.builder()
                        .bookNameEn(it.getBook().getNameEn())
                        .bookNameUk(it.getBook().getNameUk())
                        .bookItemId(it.getId())
                        .bookId(it.getBook().getId())
                        .orderId(it.getOrder().getId())
                        .quantity(it.getQuantity())
                        .unitPrice(it.getUnitPrice())
                        .build()
                ).toList();

        return OrderDTO.builder()
                .orderId(order.getId())
                .clientEmail(clientEmail)
                .employeeEmail(employeeEmail)
                .orderDate(LocalDateTime.ofInstant(order.getOrderDate(), ZoneId.systemDefault()))
                .status(order.getStatus())
                .price(order.getPrice())
                .bookItems(items)
                .build();
    }
}
