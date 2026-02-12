package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.validation.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@PasswordMatches(message = "{auth.password.confirm.mismatch}")
public class PasswordChangeDTO {
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
}
