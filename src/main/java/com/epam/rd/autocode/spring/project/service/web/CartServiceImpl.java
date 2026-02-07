package com.epam.rd.autocode.spring.project.service.web;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartServiceImpl implements CartService {
    private final Map<String, Map<String, Integer>> carts = new ConcurrentHashMap<>();

    @Override
    public void add(String email, String bookName) {
        carts.computeIfAbsent(email, x -> new ConcurrentHashMap<>()).merge(bookName, 1, Integer::sum);
    }

    @Override
    public Map<String, Integer> get(String email) {
        return carts.getOrDefault(email, Map.of());
    }

    @Override
    public void clear(String email) {
        carts.remove(email);
    }
}
