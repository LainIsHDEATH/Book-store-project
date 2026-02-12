package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmployeeProfileDTO {

    @NotBlank(message = "{employee.name.notBlank}")
    @Size(min = 2, max = 64, message = "{employee.name.size}")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЇїІіЄєҐґ'\\- ]+$",
            message = "{employee.name.pattern}"
    )
    private String name;

    @Pattern(
            regexp = "^$|^\\+?[0-9\\-() ]{7,15}$",
            message = "{employee.phone.pattern}"
    )
    private String phone;

    @PastOrPresent(message = "{employee.birthDate.pastOrPresent}")
    private LocalDate birthDate;
}