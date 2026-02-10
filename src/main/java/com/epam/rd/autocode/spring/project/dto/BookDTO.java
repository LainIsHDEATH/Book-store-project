package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookDTO {
    private Long id;

    @NotBlank
    private String nameEn;
    @NotBlank
    private String nameUk;
    @NotBlank
    private String authorEn;
    @NotBlank
    private String authorUk;
    @NotBlank
    private String descriptionEn;
    @NotBlank
    private String descriptionUk;

    @NotNull
    private Genre genre;
    @NotNull
    private AgeGroup ageGroup;
    @NotNull
    private Language language;

    @NotNull
    @PastOrPresent
    private LocalDate publicationDate;

    @NotNull
    @Positive
    private Integer pages;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @PositiveOrZero
    private Integer stockCount;
}
