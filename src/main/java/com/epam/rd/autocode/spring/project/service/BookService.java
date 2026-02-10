package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import org.springframework.data.domain.Page;

public interface BookService {
    Page<BookDTO> search(String keyword, Genre genre, AgeGroup ageGroup, Language language, int page, String sort);
    BookDTO getById(Long id);
    BookDTO save(BookDTO dto);
    void delete(Long id);
}
