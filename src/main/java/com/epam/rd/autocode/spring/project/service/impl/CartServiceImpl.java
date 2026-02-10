package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.CartStateDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.CartItem;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final HttpSession session;
    private final BookRepository bookRepository;

    @SuppressWarnings("unchecked")
    private Map<Long, CartItem> getCart() {
        Object obj = session.getAttribute("CART");
        if (obj == null) {
            Map<Long, CartItem> cart = new HashMap<>();
            session.setAttribute("CART", cart);
            return cart;
        }
        return (Map<Long, CartItem>) obj;
    }

    @Override
    public CartStateDTO getState() {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        List<BookItemDTO> items = getCart().values().stream().map(it -> new BookItemDTO(
                it.getBookId(),
                it.getTitle(),
                it.getQuantity(),
                it.getUnitPrice(),
                it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()))
        )).toList();
        BigDecimal total = items.stream().map(BookItemDTO::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartStateDTO(items, total);
    }

    @Override
    public void add(Long bookId) {
        Book b = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));
        String title = "uk".equals(LocaleContextHolder.getLocale().getLanguage()) ? b.getNameUk() : b.getNameEn();
        Map<Long, CartItem> cart = getCart();
        CartItem current = cart.get(bookId);
        if (current == null) {
            cart.put(bookId, new CartItem(bookId, title, b.getPrice(), 1));
            return;
        }
        current.setQuantity(current.getQuantity() + 1);
    }

    @Override
    public void minus(Long bookId) {
        Map<Long, CartItem> cart = getCart();
        CartItem current = cart.get(bookId);
        if (current == null) return;
        if (current.getQuantity() <= 1) {
            cart.remove(bookId);
            return;
        }
        current.setQuantity(current.getQuantity() - 1);
    }

    @Override
    public void remove(Long bookId) { getCart().remove(bookId); }

    @Override
    public void clear() { getCart().clear(); }
}
