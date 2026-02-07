package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(BookServiceImpl::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        return toDto(book);
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO book) {
        Book entity = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        apply(entity, book);
        log.info("Book updated: {}", entity.getName());
        return toDto(bookRepository.save(entity));
    }

    @Override
    public void deleteBookByName(String name) {
        Book entity = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        bookRepository.delete(entity);
        log.info("Book deleted: {}", name);
    }

    @Override
    public BookDTO addBook(BookDTO book) {
        if (bookRepository.existsByName(book.getName())) {
            throw new AlreadyExistException("Book already exists: " + book.getName());
        }
        Book saved = bookRepository.save(toEntity(book));
        log.info("Book created: {}", saved.getName());
        return toDto(saved);
    }

    @Override
    public Page<BookDTO> search(String query, String genre, Language language, Pageable pageable) {
        return bookRepository.search(query, genre, language, pageable).map(BookServiceImpl::toDto);
    }

    @Override
    public Set<String> getGenres() {
        return bookRepository.findAll().stream().map(Book::getGenre).collect(Collectors.toSet());
    }

    private static void apply(Book b, BookDTO d) { b.setName(d.getName()); b.setGenre(d.getGenre()); b.setAgeGroup(d.getAgeGroup()); b.setPrice(d.getPrice()); b.setPublicationDate(d.getPublicationDate()); b.setAuthor(d.getAuthor()); b.setPages(d.getPages()); b.setCharacteristics(d.getCharacteristics()); b.setDescription(d.getDescription()); b.setLanguage(d.getLanguage()); }

    private static BookDTO toDto(Book b) { return new BookDTO(b.getName(), b.getGenre(), b.getAgeGroup(), b.getPrice(), b.getPublicationDate(), b.getAuthor(), b.getPages(), b.getCharacteristics(), b.getDescription(), b.getLanguage()); }

    private static Book toEntity(BookDTO d) { return new Book(null, d.getName(), d.getGenre(), d.getAgeGroup(), d.getPrice(), d.getPublicationDate(), d.getAuthor(), d.getPages(), d.getCharacteristics(), d.getDescription(), d.getLanguage()); }
}
