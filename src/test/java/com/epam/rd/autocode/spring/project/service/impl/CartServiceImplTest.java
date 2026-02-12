package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.CartStateDTO;
import com.epam.rd.autocode.spring.project.exception.LimitException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.BookItemRepository;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock ClientRepository clientRepository;
    @Mock OrderRepository orderRepository;
    @Mock BookItemRepository bookItemRepository;
    @Mock BookRepository bookRepository;

    @InjectMocks CartServiceImpl cartService;

    @Test
    void getState_shouldThrow_whenClientNotFound() {
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cartService.getState("a@a"));
    }

    @Test
    void getState_shouldReturnEmpty_whenNoCartOrder() {
        Client c = mock(Client.class);
        when(c.getId()).thenReturn(1L);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));
        when(orderRepository.findByClientIdAndStatus(1L, OrderStatus.CART)).thenReturn(Optional.empty());

        CartStateDTO state = cartService.getState("a@a");

        assertNotNull(state);
        assertTrue(state.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, state.getTotal());
    }

    @Test
    void getState_shouldComputeTotal_andMapItems() {
        Client c = mock(Client.class);
        when(c.getId()).thenReturn(1L);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        Order order = mock(Order.class);
        when(order.getId()).thenReturn(10L);

        Book book = mock(Book.class);
        when(book.getId()).thenReturn(7L);
        when(book.getNameEn()).thenReturn("EN");
        when(book.getNameUk()).thenReturn("UK");

        BookItem item = mock(BookItem.class);
        when(item.getId()).thenReturn(100L);
        when(item.getBook()).thenReturn(book);
        when(item.getOrder()).thenReturn(order);
        when(item.getQuantity()).thenReturn(2);
        when(item.getUnitPrice()).thenReturn(new BigDecimal("5.00"));

        when(order.getItems()).thenReturn(List.of(item));
        when(orderRepository.findByClientIdAndStatus(1L, OrderStatus.CART)).thenReturn(Optional.of(order));

        CartStateDTO state = cartService.getState("a@a");

        assertEquals(new BigDecimal("10.00"), state.getTotal());
        assertEquals(1, state.getItems().size());
        assertEquals("EN", state.getItems().get(0).getBookNameEn());
        assertEquals(2, state.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("5.00"), state.getItems().get(0).getUnitPrice());
    }

    @Test
    void add_shouldCreateCart_whenAbsent_andAddItem() {
        Client c = mock(Client.class);
        when(c.getId()).thenReturn(1L);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));
        when(orderRepository.findByClientIdAndStatus(1L, OrderStatus.CART)).thenReturn(Optional.empty());

        Order savedOrder = mock(Order.class);
        when(savedOrder.getId()).thenReturn(10L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Book book = mock(Book.class);
        when(book.getId()).thenReturn(7L);
        when(book.getStockCount()).thenReturn(100);
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));

        BookItem item = mock(BookItem.class);
        when(item.getQuantity()).thenReturn(0);
        when(item.getUnitPrice()).thenReturn(new BigDecimal("9.99"));
        when(bookItemRepository.findByOrderIdAndBookId(10L, 7L)).thenReturn(Optional.of(item));

        cartService.add("a@a", 7L);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(bookItemRepository).save(item);
        verify(item).setQuantity(1);
        verify(savedOrder).incrementPrice(new BigDecimal("9.99"));
    }

    @Test
    void add_shouldThrowLimit_whenStockExceeded() {
        Client c = mock(Client.class);
        when(c.getId()).thenReturn(1L);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        Order order = mock(Order.class);
        when(order.getId()).thenReturn(10L);
        when(orderRepository.findByClientIdAndStatus(1L, OrderStatus.CART)).thenReturn(Optional.of(order));

        Book book = mock(Book.class);
        when(book.getId()).thenReturn(7L);
        when(book.getStockCount()).thenReturn(1);
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));

        BookItem item = mock(BookItem.class);
        when(item.getQuantity()).thenReturn(1);
        when(bookItemRepository.findByOrderIdAndBookId(10L, 7L)).thenReturn(Optional.of(item));

        assertThrows(LimitException.class, () -> cartService.add("a@a", 7L));

        verify(bookItemRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void minus_shouldDeleteItem_whenQuantityBecomesZero() {
        Client c = mock(Client.class);
        when(c.getId()).thenReturn(1L);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        Order order = mock(Order.class);
        when(order.getId()).thenReturn(10L);
        when(orderRepository.findByClientIdAndStatus(1L, OrderStatus.CART)).thenReturn(Optional.of(order));
        when(order.getItems()).thenReturn(List.of());

        Book book = mock(Book.class);
        when(book.getId()).thenReturn(7L);
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));

        BookItem item = mock(BookItem.class);

        AtomicInteger q = new AtomicInteger(1);
        when(item.getQuantity()).thenAnswer(inv -> q.get());
        doAnswer(inv -> { q.set(inv.getArgument(0)); return null; }).when(item).setQuantity(anyInt());

        when(item.getUnitPrice()).thenReturn(new BigDecimal("5.00"));
        when(bookItemRepository.findByOrderIdAndBookId(10L, 7L)).thenReturn(Optional.of(item));

        cartService.minus("a@a", 7L);

        verify(order).decrementPrice(new BigDecimal("5.00"));
        verify(bookItemRepository).delete(item);
        verify(bookItemRepository).flush();
        verify(orderRepository).delete(order);
    }

    @Test
    void clear_shouldDeleteCartByClientIdAndStatus() {
        Client c = mock(Client.class);
        when(c.getId()).thenReturn(1L);
        when(clientRepository.findByEmail("a@a")).thenReturn(Optional.of(c));

        cartService.clear("a@a");

        verify(orderRepository).deleteByClientIdAndStatus(1L, OrderStatus.CART);
    }
}