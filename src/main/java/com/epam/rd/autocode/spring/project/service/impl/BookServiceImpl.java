package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<BookDTO> getAllBooks(){
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Page<BookDTO> search(String keyword, AgeGroup ageGroup, Language language, String author, String genre, int page, Sort sort) {
        Sort safeSort = (sort == null || sort.isUnsorted())
                ? Sort.by("id").ascending()
                : sort;
        return bookRepository
                .findWithFilters(keyword, ageGroup, language, author, genre, PageRequest.of(page, 6, safeSort))
                .map(this::toDto);
    }

    @Override
    public BookDTO getBookById(Long id) {
        return toDto(bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id)));
    }

    @Override
    @Transactional
    public void addBook(BookDTO dto){
        Book entity = dto.getId() == null
                ? new Book()
                : bookRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Book not found: " + dto.getId()));
//        modelMapper.map(dto, entity);
        entity.setNameEn(dto.getNameEn());
        entity.setNameUk(dto.getNameUk());
        entity.setAuthorEn(dto.getAuthorEn());
        entity.setAuthorUk(dto.getAuthorUk());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setDescriptionUk(dto.getDescriptionUk());
        entity.setGenreEn(dto.getGenreEn());
        entity.setGenreUk(dto.getGenreUk());
        entity.setAgeGroup(dto.getAgeGroup());
        entity.setLanguage(dto.getLanguage());
        entity.setPublicationDate(dto.getPublicationDate());
        entity.setPages(dto.getPages());
        entity.setPrice(dto.getPrice());
        entity.setStockCount(dto.getStockCount());
        bookRepository.save(entity);
    }

    @Override
    @Transactional
    public BookDTO save(BookDTO dto) {
        Book entity = dto.getId() == null
                ? new Book()
                : bookRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Book not found: " + dto.getId()));
//        modelMapper.map(dto, entity);
        entity.setNameEn(dto.getNameEn());
        entity.setNameUk(dto.getNameUk());
        entity.setAuthorEn(dto.getAuthorEn());
        entity.setAuthorUk(dto.getAuthorUk());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setDescriptionUk(dto.getDescriptionUk());
        entity.setGenreEn(dto.getGenreEn());
        entity.setGenreUk(dto.getGenreUk());
        entity.setAgeGroup(dto.getAgeGroup());
        entity.setLanguage(dto.getLanguage());
        entity.setPublicationDate(dto.getPublicationDate());
        entity.setPages(dto.getPages());
        entity.setPrice(dto.getPrice());
        entity.setStockCount(dto.getStockCount());
        return toDto(bookRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public List<String> findAllAuthors(){
        return bookRepository.findAllAuthors();
    }

    @Override
    public List<Object[]> getAllGenresTranslated(){
        return bookRepository.findAllGenresWithTranslations();
    }

    public List<String> findAllLanguages(){
        return bookRepository.findAllLanguages();
    }

    @Override
    public List<String> getAllEnGenres(){
        return bookRepository.findAllEnGenres();
    }

    @Override
    @Transactional
    public void updateBookId(Long id, BookDTO dto){
        Book entity = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        entity.setNameEn(dto.getNameEn());
        entity.setNameUk(dto.getNameUk());
        entity.setAuthorEn(dto.getAuthorEn());
        entity.setAuthorUk(dto.getAuthorUk());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setDescriptionUk(dto.getDescriptionUk());
        entity.setGenreEn(dto.getGenreEn());
        entity.setGenreUk(dto.getGenreUk());
        entity.setAgeGroup(dto.getAgeGroup());
        entity.setLanguage(dto.getLanguage());
        entity.setPublicationDate(dto.getPublicationDate());
        entity.setPages(dto.getPages());
        entity.setPrice(dto.getPrice());
        entity.setStockCount(dto.getStockCount());
        bookRepository.save(entity);
    }

    @Override
    @Transactional
    public void updateBookByName(String nameEn, BookDTO dto){
        Book entity = bookRepository.findByNameEn(nameEn)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        entity.setNameEn(dto.getNameEn());
        entity.setNameUk(dto.getNameUk());
        entity.setAuthorEn(dto.getAuthorEn());
        entity.setAuthorUk(dto.getAuthorUk());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setDescriptionUk(dto.getDescriptionUk());
        entity.setGenreEn(dto.getGenreEn());
        entity.setGenreUk(dto.getGenreUk());
        entity.setAgeGroup(dto.getAgeGroup());
        entity.setLanguage(dto.getLanguage());
        entity.setPublicationDate(dto.getPublicationDate());
        entity.setPages(dto.getPages());
        entity.setPrice(dto.getPrice());
        entity.setStockCount(dto.getStockCount());
        bookRepository.save(entity);
    }

    private BookDTO toDto(Book b) {
        BookDTO dto = new BookDTO();
        dto.setId(b.getId());
        dto.setNameEn(b.getNameEn());
        dto.setNameUk(b.getNameUk());
        dto.setAuthorEn(b.getAuthorEn());
        dto.setAuthorUk(b.getAuthorUk());
        dto.setDescriptionEn(b.getDescriptionEn());
        dto.setDescriptionUk(b.getDescriptionUk());
        dto.setGenreEn(b.getGenreEn());
        dto.setGenreUk(b.getGenreUk());
        dto.setAgeGroup(b.getAgeGroup());
        dto.setLanguage(b.getLanguage());
        dto.setPublicationDate(b.getPublicationDate());
        dto.setPages(b.getPages());
        dto.setPrice(b.getPrice());
        dto.setStockCount(b.getStockCount());
        return dto;
    }
}