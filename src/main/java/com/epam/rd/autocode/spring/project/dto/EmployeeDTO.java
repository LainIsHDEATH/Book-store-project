package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO{
    private Long id;

    @NotBlank
    @Email
    private String email;

    @Size(min = 8, max = 20, message = "Від 8 до 20 символів")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "Має містити цифру та велику літеру"
    )
    private String password;

    @NotBlank(message = "")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЇїІіЄєҐґ'\\- ]+$",
            message = ""
    )
    private String name;

    @Pattern(
            regexp = "^\\+?[0-9\\-() ]{7,15}$",
            message = ""
    )
    private String phone;

    private Boolean blocked = false;

    @NotNull
    private LocalDate birthDate;

}
