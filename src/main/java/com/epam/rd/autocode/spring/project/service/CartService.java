package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.CartStateDTO;

public interface CartService {
    CartStateDTO getState(String email);
    void add(String email, Long bookId);
    void minus(String email, Long bookId);
    void remove(Long bookId);
    void clear(String email);
}
