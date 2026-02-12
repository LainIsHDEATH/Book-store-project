package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookItemRepository extends JpaRepository<BookItem, Long> {
    Optional<BookItem> findByOrderIdAndBookId(Long orderId, Long bookId);
}
