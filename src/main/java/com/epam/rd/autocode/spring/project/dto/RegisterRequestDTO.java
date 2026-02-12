package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.validation.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@PasswordMatches(message = "{auth.password.confirm.mismatch}")
public class RegisterRequestDTO {
    @NotNull
    private Role role;

    @NotBlank(message = "{auth.email.notBlank}")
    @Email(message = "{auth.email.invalid}")
    @Size(max = 255, message = "{auth.email.size}")
    private String email;

    @NotBlank(message = "{employee.name.notBlank}")
    @Size(min = 2, max = 64, message = "{employee.name.size}")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЇїІіЄєҐґ'\\- ]+$",
            message = "{employee.name.pattern}"
    )
    private String name;

    @NotBlank(message = "{auth.password.notBlank}")
    @Size(min = 8, max = 20, message = "{auth.password.size}")
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "{auth.password.pattern}"
    )
    private String password;


    @NotBlank(message = "{auth.confirmPassword.notBlank}")
    @Size(min = 8, max = 20, message = "{auth.password.size}")
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "{auth.password.pattern}"
    )
    private String confirmPassword;

    private String phone;
    private String birthDate;
}