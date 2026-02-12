package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.InvalidBalanceException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock ClientRepository clientRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock BookRepository bookRepository;

    @InjectMocks OrderServiceImpl orderService;

    @Test
    void getMyOrders_shouldFilterOutCart() {
        Client c = new Client(1L, "a@a", "p", "N", BigDecimal.ZERO);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        Order cart = mock(Order.class);
        when(cart.getStatus()).thenReturn(OrderStatus.CART);

        Order paid = mock(Order.class);
        when(paid.getStatus()).thenReturn(OrderStatus.PAID);
        when(paid.getOrderDate()).thenReturn(Instant.now());
        when(paid.getItems()).thenReturn(List.of());
        when(paid.getClient()).thenReturn(c);

        when(orderRepository.findByClientId(1L)).thenReturn(List.of(cart, paid));

        List<OrderDTO> res = orderService.getMyOrders("a@a");

        assertEquals(1, res.size());
        assertEquals(OrderStatus.PAID, res.get(0).getStatus());
    }

    @Test
    void checkout_shouldThrow_whenInsufficientBalance() {
        Client c = new Client(1L, "a@a", "p", "N", new BigDecimal("5.00"));
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        Order order = mock(Order.class);
        when(order.getPrice()).thenReturn(new BigDecimal("10.00"));
        when(orderRepository.findByClientIdAndStatus(1L, OrderStatus.CART)).thenReturn(Optional.of(order));

        assertThrows(InvalidBalanceException.class, () -> orderService.checkout("a@a"));
    }

    @Test
    void checkout_shouldSetPaid_andDecrementBalance() {
        Client c = new Client(1L, "a@a", "p", "N", new BigDecimal("20.00"));
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        Order order = new Order();
        order.setId(10L);
        order.setClient(c);
        order.setStatus(OrderStatus.CART);
        order.setPrice(new BigDecimal("7.00"));
        order.setOrderDate(Instant.now());
        order.setItems(List.of());

        when(orderRepository.findByClientIdAndStatus(1L, OrderStatus.CART)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.checkout("a@a");

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(new BigDecimal("13.00"), c.getBalance());
        assertNotNull(dto.getOrderDate());

        verify(orderRepository).save(order);
        verify(clientRepository).save(c);
    }

    @Test
    void updateStatus_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.updateStatus(1L, OrderStatus.SHIPPED, "e@e"));
    }

    @Test
    void updateStatus_shouldSetEmployeeAndSave() {
        Order order = new Order();
        order.setId(1L);

        Employee e = new Employee();
        e.setEmail("e@e");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("e@e")).thenReturn(Optional.of(e));

        orderService.updateStatus(1L, OrderStatus.SHIPPED, "e@e");

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(e, order.getEmployee());
        verify(orderRepository).save(order);
    }
}