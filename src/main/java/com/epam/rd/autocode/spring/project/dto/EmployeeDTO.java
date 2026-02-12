package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO{
    private Long id;

    @NotBlank(message = "{auth.email.notBlank}")
    @Email(message = "{auth.email.invalid}")
    @Size(max = 255, message = "{auth.email.size}")
    private String email;

    @NotBlank(message = "{auth.password.notBlank}")
    @Size(min = 8, max = 20, message = "{auth.password.size}")
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "{auth.password.pattern}"
    )
    private String password;

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

    private Boolean blocked = false;

    @PastOrPresent(message = "{employee.birthDate.pastOrPresent}")
    private LocalDate birthDate;

}
