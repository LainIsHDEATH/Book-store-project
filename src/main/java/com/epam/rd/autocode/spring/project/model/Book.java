package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BOOKS")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "GENRE", nullable = false)
    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "AGE_GROUP", nullable = false)
    private AgeGroup ageGroup;

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;

    @Column(name = "PUBLICATION_YEAR", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Column(name = "NUMBER_OF_PAGES", nullable = false)
    private Integer pages;

    @Column(name = "CHARACTERISTICS")
    private String characteristics;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "LANGUAGE", nullable = false)
    private Language language;
}
