package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<BookDTO> search(String keyword, Genre genre, AgeGroup ageGroup, Language language, int page, String sort) {
        Sort sortObj = "date".equals(sort) ? Sort.by("publicationDate").descending() : Sort.by("nameEn").ascending();
        return bookRepository.search(keyword, genre, ageGroup, language, PageRequest.of(page, 8, sortObj)).map(this::toDto);
    }

    @Override
    public BookDTO getById(Long id) {
        return toDto(bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found")));
    }

    @Override
    public BookDTO save(BookDTO dto) {
        Book entity = dto.getId() == null ? new Book() : bookRepository.findById(dto.getId()).orElseThrow(() -> new NotFoundException("Book not found"));
//        modelMapper.map(dto, entity);
        entity.setNameEn(dto.getNameEn());
        entity.setNameUk(dto.getNameUk());
        entity.setAuthorEn(dto.getAuthorEn());
        entity.setAuthorUk(dto.getAuthorUk());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setDescriptionUk(dto.getDescriptionUk());
        entity.setGenre(dto.getGenre());
        entity.setAgeGroup(dto.getAgeGroup());
        entity.setLanguage(dto.getLanguage());
        entity.setPublicationDate(dto.getPublicationDate());
        entity.setPages(dto.getPages());
        entity.setPrice(dto.getPrice());
        entity.setStockCount(dto.getStockCount());
        return toDto(bookRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
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
        dto.setGenre(b.getGenre());
        dto.setAgeGroup(b.getAgeGroup());
        dto.setLanguage(b.getLanguage());
        dto.setPublicationDate(b.getPublicationDate());
        dto.setPages(b.getPages());
        dto.setPrice(b.getPrice());
        dto.setStockCount(b.getStockCount());
        return dto;
    }
}
