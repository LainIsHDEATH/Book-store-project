package com.epam.rd.autocode.spring.project.repo.auth;

import com.epam.rd.autocode.spring.project.model.auth.OrderStatusEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusEntryRepository extends JpaRepository<OrderStatusEntry, Long> {
}
