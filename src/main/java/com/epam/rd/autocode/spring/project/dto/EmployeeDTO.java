package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeDTO {
    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @Pattern(regexp = "^\\+?[0-9\\-() ]{7,15}$")
    private String phone;

    @Past
    private LocalDate birthDate;

    @Size(min = 8, max = 72)
    private String password;

    private boolean blocked;
}
