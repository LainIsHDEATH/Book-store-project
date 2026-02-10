package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotBlank(message = "...")
    @Size(min = 2, max = 255)
    private String nameEn;

    @NotBlank(message = "...")
    @Size(min = 2, max = 255)
    private String nameUk;

    @NotBlank(message = "...")
    private String genreEn;

    @NotBlank(message = "...")
    private String genreUk;

    private AgeGroup ageGroup;

    @Min(value = 0, message = "...")
    private BigDecimal price;

    @PastOrPresent(message = "...")
    private LocalDate publicationDate;

    @NotBlank(message = "...")
    private String authorEn;

    @NotBlank(message = "...")
    private String authorUk;

    @Min(1)
    private Integer pages;

    private String characteristics;
    private String descriptionEn;
    private String descriptionUk;

    @Min(value = 0, message = "...")
    private Integer stockCount;

    private Language language;
}
