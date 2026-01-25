package com.epam.rd.autocode.spring.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO{

    @NotBlank
    @Email
    private String clientEmail;

    @Email
    private String employeeEmail;

    @NotNull
    private LocalDateTime orderDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    @Valid
    private List<BookItemDTO> bookItems;
}
