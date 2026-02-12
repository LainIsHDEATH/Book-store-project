package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.CartStateDTO;
import com.epam.rd.autocode.spring.project.exception.LimitException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.BookItemRepository;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;
    private final BookItemRepository bookItemRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public CartStateDTO getState(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        return orderRepository.findByClientIdAndStatus(client.getId(), OrderStatus.CART)
                .map(order -> {
                    List<BookItem> bookItems = order.getItems();
                    if (bookItems == null || bookItems.isEmpty()) {
                        return new CartStateDTO(List.of(), BigDecimal.ZERO);
                    }

                    BigDecimal totalPrice = bookItems.stream()
                            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    List<BookItemDTO> items = bookItems.stream()
                            .map(it -> BookItemDTO.builder()
                                    .bookNameEn(it.getBook().getNameEn())
                                    .bookNameUk(it.getBook().getNameUk())
                                    .bookItemId(it.getId())
                                    .bookId(it.getBook().getId())
                                    .orderId(it.getOrder().getId())
                                    .quantity(it.getQuantity())
                                    .unitPrice(it.getUnitPrice())
                                    .build())
                            .toList();

                    return new CartStateDTO(items, totalPrice);
                })
                .orElseGet(() -> new CartStateDTO(List.of(), BigDecimal.ZERO));
    }

    @Override
    @Transactional
    public void add(String email, Long bookId) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        Order order = orderRepository.findByClientIdAndStatus(client.getId(), OrderStatus.CART)
                .orElseGet(() -> Order.builder()
                        .client(client)
                        .status(OrderStatus.CART)
                        .price(BigDecimal.ZERO)
                        .orderDate(Instant.now())
                        .build());
        if (order.getId() == null) {
            order = orderRepository.save(order);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        Order finalOrder = order;
        BookItem item = bookItemRepository.findByOrderIdAndBookId(order.getId(), book.getId())
                .orElseGet(() -> BookItem.builder()
                        .order(finalOrder)
                        .book(book)
                        .unitPrice(book.getPrice())
                        .quantity(0)
                        .build());

        Integer finalQuantity = item.getQuantity() + 1;
        Integer stockCount = book.getStockCount();
        if (finalQuantity > stockCount) {
            throw new LimitException("Stock exceeded.");
        }

        item.setQuantity(finalQuantity);
        order.incrementPrice(item.getUnitPrice());
        bookItemRepository.save(item);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void minus(String email, Long bookId) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        Order order = orderRepository.findByClientIdAndStatus(client.getId(), OrderStatus.CART)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + email));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        BookItem item = bookItemRepository.findByOrderIdAndBookId(order.getId(), book.getId())
                .orElseThrow(() -> new NotFoundException("BookItem not found"));

        item.setQuantity(item.getQuantity() - 1);
        order.decrementPrice(item.getUnitPrice());
        if (item.getQuantity() <= 0) {
            bookItemRepository.delete(item);
            bookItemRepository.flush();
            if (order.getItems().isEmpty()) {
                orderRepository.delete(order);
            }
        } else {
            bookItemRepository.save(item);
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public void remove(Long bookItemId) {
        bookItemRepository.deleteById(bookItemId);
    }

    @Override
    @Transactional
    public void clear(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        orderRepository.deleteByClientIdAndStatus(client.getId(), OrderStatus.CART);
    }
}