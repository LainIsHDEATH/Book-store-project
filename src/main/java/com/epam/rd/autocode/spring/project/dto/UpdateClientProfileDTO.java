package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClientProfileDTO {

    @NotBlank(message = "{client.name.notBlank}")
    @Size(min = 2, max = 64, message = "{client.name.size}")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЇїІіЄєҐґ'\\- ]+$",
            message = "{client.name.pattern}"
    )
    private String name;
}
