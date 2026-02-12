package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookService {
    List<BookDTO> getAllBooks();
    Page<BookDTO> search(String keyword, AgeGroup ageGroup, Language language, String author, String genre, int page, Sort sort);
    BookDTO getBookById(Long id);
    BookDTO save(BookDTO dto);
    public List<Object[]> getAllGenresTranslated();
    List<String> getAllEnGenres();

    void addBook(BookDTO bookDTO);
    void updateBookId(Long id, BookDTO bookDTO);
    void updateBookByName(String nameEn, BookDTO bookDTO);
    void deleteBookById(Long id);
}