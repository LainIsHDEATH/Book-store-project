package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Entity;
import lombok.*;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_uk")
    private String nameUk;

    @Column(name = "genre_en")
    private String genreEn;

    @Column(name = "genre_uk")
    private String genreUk;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    private AgeGroup ageGroup;

    private BigDecimal price;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "author_en")
    private String authorEn;

    @Column(name = "author_uk")
    private String authorUk;

    @Column(name = "pages")
    private Integer pages;

    @Column(columnDefinition = "TEXT")
    private String characteristics;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_uk", columnDefinition = "TEXT")
    private String descriptionUk;

    private Integer stockCount;

    @Enumerated(EnumType.STRING)
    private Language language;
}
