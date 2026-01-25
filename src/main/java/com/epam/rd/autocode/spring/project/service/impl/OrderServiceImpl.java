package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientRepository clientRepository,
                            EmployeeRepository employeeRepository,
                            BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
    }

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
            employee = employeeRepository.findByEmail(dto.getEmployeeEmail())
                    .orElseThrow(() -> new NotFoundException("Employee not found: " + dto.getEmployeeEmail()));
        }

        LocalDateTime orderDate = dto.getOrderDate() != null ? dto.getOrderDate() : LocalDateTime.now();

        Order order = new Order(null, client, employee, orderDate, BigDecimal.ZERO, new ArrayList<>());
        List<BookItem> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;
        for (BookItemDTO itemDto : dto.getBookItems()) {
            Book book = bookRepository.findByName(itemDto.getBookName())
                    .orElseThrow(() -> new NotFoundException("Book not found: " + itemDto.getBookName()));

            BookItem item = new BookItem(null, itemDto.getQuantity(), book, order);
            items.add(item);

            BigDecimal line = book.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            total = total.add(line);
        }

        order.setBookItems(items);
        order.setPrice(total);

        // Optional "store logic": decrease balance if enough
        if (client.getBalance() != null && client.getBalance().compareTo(total) >= 0) {
            client.setBalance(client.getBalance().subtract(total));
            clientRepository.save(client);
        }

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    private static OrderDTO toDto(Order o) {
        String clientEmail = o.getClient() != null ? o.getClient().getEmail() : null;
        String employeeEmail = o.getEmployee() != null ? o.getEmployee().getEmail() : null;

        List<BookItemDTO> items = o.getBookItems() == null ? List.of() :
                o.getBookItems().stream()
                        .map(bi -> new BookItemDTO(bi.getBook().getName(), bi.getQuantity()))
                        .toList();

        return new OrderDTO(clientEmail, employeeEmail, o.getOrderDate(), o.getPrice(), items);
    }
}
