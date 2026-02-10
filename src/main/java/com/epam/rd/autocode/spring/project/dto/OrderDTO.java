package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String clientEmail;
    private String employeeEmail;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal price;
    private List<BookItemDTO> bookItems;
}
