package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO{

    Long id;

    @NotBlank(message = "{auth.email.notBlank}")
    @Email(message = "{auth.email.invalid}")
    @Size(max = 255, message = "{auth.email.size}")
    private String email;

    private String password;

    @NotBlank(message = "{employee.name.notBlank}")
    @Size(min = 2, max = 64, message = "{employee.name.size}")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЇїІіЄєҐґ'\\- ]+$",
            message = "{employee.name.pattern}"
    )
    private String name;

    @NotNull(message = "{recharge.amount.required}")
    @PositiveOrZero(message = "{balance.amount.positive}")
    private BigDecimal balance;

    private Boolean isBlocked;
}
