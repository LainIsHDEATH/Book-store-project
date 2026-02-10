package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.CartStateDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.InvalidBalanceException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.CartService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;

    @Override
    public List<OrderDTO> getMyOrders(String email) {
        return orderRepository.findByClient_EmailOrderByOrderDateDesc(email).stream().map(this::toDto).toList();
    }

    @Override
    public List<OrderDTO> getAll() {
        return orderRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public OrderDTO getById(Long id) {
        return toDto(orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found")));
    }

    @Override
    @Transactional
    public OrderDTO checkout(String email) {
        Client client = clientRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Client not found"));
        CartStateDTO cart = cartService.getState();
        if (client.getBalance().compareTo(cart.getTotal()) < 0) throw new InvalidBalanceException("Not enough balance");

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        order.setPrice(cart.getTotal());

        List<BookItem> items = new ArrayList<>();
        for (BookItemDTO item : cart.getItems()) {
            Book b = bookRepository.findById(item.getBookId()).orElseThrow(() -> new NotFoundException("Book not found"));
            BookItem bi = new BookItem();
            bi.setBook(b);
            bi.setOrder(order);
            bi.setQuantity(item.getQuantity());
            bi.setUnitPrice(item.getUnitPrice());
            items.add(bi);
        }
        order.setItems(items);

        client.setBalance(client.getBalance().subtract(cart.getTotal()));
        clientRepository.save(client);
        Order saved = orderRepository.save(order);
        cartService.clear();
        return toDto(saved);
    }

    @Override
    public void cancelMyOrder(String email, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!order.getClient().getEmail().equals(email)) throw new NotFoundException("Order not found");
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus status, String employeeEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        Employee employee = employeeRepository.findByEmail(employeeEmail).orElseThrow(() -> new NotFoundException("Employee not found"));
        order.setEmployee(employee);
        order.setStatus(status);
        orderRepository.save(order);
    }

    private OrderDTO toDto(Order o) {
        OrderDTO dto = new OrderDTO();
        dto.setId(o.getId());
        dto.setClientEmail(o.getClient().getEmail());
        dto.setEmployeeEmail(o.getEmployee() == null ? null : o.getEmployee().getEmail());
        dto.setOrderDate(o.getOrderDate());
        dto.setStatus(o.getStatus());
        dto.setPrice(o.getPrice());
        dto.setBookItems(o.getItems().stream().map(i -> new BookItemDTO(i.getBook().getId(), i.getBook().getNameEn(), i.getQuantity(), i.getUnitPrice(), i.getUnitPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity())))).toList());
        return dto;
    }
}
