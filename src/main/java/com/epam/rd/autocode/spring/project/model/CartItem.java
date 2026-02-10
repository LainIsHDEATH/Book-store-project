package com.epam.rd.autocode.spring.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItem implements Serializable {
    private Long bookId;
    private String title;
    private BigDecimal unitPrice;
    private int quantity;
}
