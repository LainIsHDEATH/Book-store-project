package com.epam.rd.autocode.spring.project.service.web;

import java.util.Map;

public interface CartService {
    void add(String email, String bookName);
    Map<String, Integer> get(String email);
    void clear(String email);
}
