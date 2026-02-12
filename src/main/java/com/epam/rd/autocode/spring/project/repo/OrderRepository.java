package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(Long clientId);
    List<Order> findByEmployeeId(Long employeeId);
    Optional<Order> findByClientIdAndStatus(Long clientId, OrderStatus status);
    void deleteByClientIdAndStatus(Long clientId, OrderStatus status);
}
