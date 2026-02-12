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
@Builder
public class BookDTO {

    private Long id;
    private Integer cartQuantity;

    @NotBlank(message = "{book.nameEn.notBlank}")
    @Size(min = 2, max = 255, message = "{book.nameEn.size}")
    private String nameEn;

    @NotBlank(message = "{book.nameUk.notBlank}")
    @Size(min = 2, max = 255, message = "{book.nameUk.size}")
    private String nameUk;

    @NotBlank(message = "{book.authorEn.notBlank}")
    @Size(min = 2, max = 255, message = "{book.authorEn.size}")
    private String authorEn;

    @NotBlank(message = "{book.authorUk.notBlank}")
    @Size(min = 2, max = 255, message = "{book.authorUk.size}")
    private String authorUk;

    @NotBlank(message = "{book.genreEn.notBlank}")
    @Size(min = 2, max = 100, message = "{book.genreEn.size}")
    private String genreEn;

    @NotBlank(message = "{book.genreUk.notBlank}")
    @Size(min = 2, max = 100, message = "{book.genreUk.size}")
    private String genreUk;

    @Size(max = 500, message = "{book.descriptionEn.size}")
    private String descriptionEn;

    @Size(max = 500, message = "{book.descriptionUk.size}")
    private String descriptionUk;

    @NotNull(message = "{book.language.notNull}")
    private Language language;

    @NotNull(message = "{book.ageGroup.notNull}")
    private AgeGroup ageGroup;

    @NotNull(message = "{book.price.notNull}")
    @DecimalMin(value = "0.00", inclusive = true, message = "{book.price.min}")
    @Digits(integer = 10, fraction = 2, message = "{book.price.digits}")
    private BigDecimal price;

    @NotNull(message = "{book.stockCount.notNull}")
    @Min(value = 0, message = "{book.stockCount.min}")
    @Max(value = 1_000_000, message = "{book.stockCount.max}")
    private Integer stockCount;

    @NotNull(message = "{book.pages.notNull}")
    @Min(value = 1, message = "{book.pages.min}")
    @Max(value = 100_000, message = "{book.pages.max}")
    private Integer pages;

    @NotNull(message = "{book.publicationDate.notNull}")
    @PastOrPresent(message = "{book.publicationDate.pastOrPresent}")
    private LocalDate publicationDate;

    @Size(max = 500, message = "{book.characteristics.size}")
    private String characteristics;

}
