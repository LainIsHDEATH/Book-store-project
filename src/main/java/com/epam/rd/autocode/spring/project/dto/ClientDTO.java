package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientDTO {
    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 2, max = 64)
    private String name;

    @Size(min = 8, max = 72)
    private String password;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    private boolean blocked;
}
