package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.auth.OrderViewDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.auth.OrderStatus;
import com.epam.rd.autocode.spring.project.model.auth.OrderStatusEntry;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.repo.auth.OrderStatusEntryRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final OrderStatusEntryRepository orderStatusEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        return orderRepository.findByClient_Email(clientEmail).stream().map(OrderServiceImpl::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        return orderRepository.findByEmployee_Email(employeeEmail).stream().map(OrderServiceImpl::toDto).toList();
    }

    @Override
    public OrderDTO addOrder(OrderDTO dto) {
        Client client = clientRepository.findByEmail(dto.getClientEmail())
                .orElseThrow(() -> new NotFoundException("Client not found: " + dto.getClientEmail()));
        Employee employee = null;
        if (dto.getEmployeeEmail() != null && !dto.getEmployeeEmail().isBlank()) {
            employee = employeeRepository.findByEmail(dto.getEmployeeEmail()).orElseThrow(() -> new NotFoundException("Employee not found"));
        }

        Order order = new Order(null, client, employee, dto.getOrderDate() == null ? LocalDateTime.now() : dto.getOrderDate(), BigDecimal.ZERO, new ArrayList<>());
        BigDecimal total = BigDecimal.ZERO;
        List<BookItem> items = new ArrayList<>();
        for (BookItemDTO itemDto : dto.getBookItems()) {
            Book book = bookRepository.findByName(itemDto.getBookName()).orElseThrow(() -> new NotFoundException("Book not found: " + itemDto.getBookName()));
            BookItem item = new BookItem(null, itemDto.getQuantity(), book, order);
            total = total.add(book.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            items.add(item);
        }
        order.setPrice(total);
        order.setBookItems(items);
        Order saved = orderRepository.save(order);
        orderStatusEntryRepository.save(new OrderStatusEntry(saved.getId(), OrderStatus.PAID));
        log.info("Order {} created for {}", saved.getId(), client.getEmail());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderViewDTO> getOrdersForUser(String email, boolean employee) {
        List<Order> orders = employee ? orderRepository.findAll() : orderRepository.findByClient_Email(email);
        return orders.stream().map(o -> {
            var status = orderStatusEntryRepository.findById(o.getId()).map(OrderStatusEntry::getStatus).orElse(OrderStatus.PAID);
            return new OrderViewDTO(o.getId(), o.getClient().getEmail(), o.getEmployee() == null ? null : o.getEmployee().getEmail(), o.getOrderDate(), o.getPrice(), status);
        }).toList();
    }

    @Override
    public void cancelOrder(Long orderId, String actorEmail, boolean employee) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!employee && !order.getClient().getEmail().equals(actorEmail)) {
            throw new NotFoundException("Forbidden cancel operation");
        }
        orderStatusEntryRepository.save(new OrderStatusEntry(orderId, OrderStatus.CANCELED));
        log.warn("Order {} canceled by {}", orderId, actorEmail);
    }

    private static OrderDTO toDto(Order o) {
        return new OrderDTO(o.getClient() == null ? null : o.getClient().getEmail(), o.getEmployee() == null ? null : o.getEmployee().getEmail(), o.getOrderDate(), o.getPrice(),
                o.getBookItems() == null ? List.of() : o.getBookItems().stream().map(bi -> new BookItemDTO(bi.getBook().getName(), bi.getQuantity())).toList());
    }
}
