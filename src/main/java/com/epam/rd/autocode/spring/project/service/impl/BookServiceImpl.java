package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

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

        entity.setName(book.getName());
        entity.setGenre(book.getGenre());
        entity.setAgeGroup(book.getAgeGroup());
        entity.setPrice(book.getPrice());
        entity.setPublicationDate(book.getPublicationDate());
        entity.setAuthor(book.getAuthor());
        entity.setPages(book.getPages());
        entity.setCharacteristics(book.getCharacteristics());
        entity.setDescription(book.getDescription());
        entity.setLanguage(book.getLanguage());

        return toDto(bookRepository.save(entity));
    }

    @Override
    public void deleteBookByName(String name) {
        Book entity = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        bookRepository.delete(entity);
    }

    @Override
    public BookDTO addBook(BookDTO book) {
        if (bookRepository.existsByName(book.getName())) {
            throw new AlreadyExistException("Book already exists: " + book.getName());
        }
        Book saved = bookRepository.save(toEntity(book));
        return toDto(saved);
    }

    private static BookDTO toDto(Book b) {
        return new BookDTO(
                b.getName(),
                b.getGenre(),
                b.getAgeGroup(),
                b.getPrice(),
                b.getPublicationDate(),
                b.getAuthor(),
                b.getPages(),
                b.getCharacteristics(),
                b.getDescription(),
                b.getLanguage()
        );
    }

    private static Book toEntity(BookDTO d) {
        return new Book(
                null,
                d.getName(),
                d.getGenre(),
                d.getAgeGroup(),
                d.getPrice(),
                d.getPublicationDate(),
                d.getAuthor(),
                d.getPages(),
                d.getCharacteristics(),
                d.getDescription(),
                d.getLanguage()
        );
    }
}
