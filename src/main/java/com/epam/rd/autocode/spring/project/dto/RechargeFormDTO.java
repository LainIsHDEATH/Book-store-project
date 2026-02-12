package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RechargeFormDTO {

    @NotNull(message = "{recharge.amount.required}")
    @DecimalMin(value = "0.01", inclusive = true, message = "{recharge.amount.min}")
    @Digits(integer = 10, fraction = 2, message = "{recharge.amount.format}")
    private BigDecimal amount;
}
