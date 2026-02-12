package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    BookServiceImpl bookService;

    @Test
    void getAllBooks_shouldReturnDtos() {
        Book b1 = new Book();
        b1.setId(1L);
        b1.setNameEn("A");
        b1.setPrice(new BigDecimal("10.00"));

        Book b2 = new Book();
        b2.setId(2L);
        b2.setNameEn("B");
        b2.setPrice(new BigDecimal("20.00"));

        when(bookRepository.findAll()).thenReturn(List.of(b1, b2));

        List<BookDTO> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("A", result.get(0).getNameEn());
        assertEquals(new BigDecimal("10.00"), result.get(0).getPrice());
        verify(bookRepository).findAll();
        verifyNoInteractions(modelMapper); // у тебя modelMapper закомментирован
    }

    @Test
    void search_shouldUseDefaultSort_whenSortNull() {
        Page<Book> page = new PageImpl<>(List.of(new Book()));
        when(bookRepository.findWithFilters(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<BookDTO> result = bookService.search(
                "k", AgeGroup.TEEN, Language.ENGLISH, "a", "g", 2, null
        );

        assertEquals(1, result.getTotalElements());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(bookRepository).findWithFilters(eq("k"), eq(AgeGroup.TEEN), eq(Language.ENGLISH), eq("a"), eq("g"), captor.capture());

        Pageable p = captor.getValue();
        assertEquals(2, p.getPageNumber());
        assertEquals(6, p.getPageSize());
        assertTrue(p.getSort().getOrderFor("id").isAscending());
    }

    @Test
    void getBookById_shouldThrow_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.getBookById(99L));
    }

    @Test
    void getBookById_shouldReturnDto_whenFound() {
        Book b = new Book();
        b.setId(1L);
        b.setNameEn("X");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));

        BookDTO dto = bookService.getBookById(1L);

        assertEquals(1L, dto.getId());
        assertEquals("X", dto.getNameEn());
    }

    @Test
    void addBook_shouldCreateNew_whenIdNull() {
        BookDTO dto = sampleDto(null, "N1");
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        bookService.addBook(dto);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book saved = captor.getValue();
        assertNull(saved.getId());
        assertEquals("N1", saved.getNameEn());
        assertEquals(dto.getPrice(), saved.getPrice());
    }

    @Test
    void addBook_shouldUpdateExisting_whenIdPresent() {
        Book existing = new Book();
        existing.setId(10L);
        existing.setNameEn("Old");

        BookDTO dto = sampleDto(10L, "New");

        when(bookRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        bookService.addBook(dto);

        verify(bookRepository).findById(10L);
        verify(bookRepository).save(existing);
        assertEquals("New", existing.getNameEn());
    }

    @Test
    void save_shouldReturnDto() {
        BookDTO dto = sampleDto(null, "Book");

        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(123L);
            return b;
        });

        BookDTO saved = bookService.save(dto);

        assertEquals(123L, saved.getId());
        assertEquals("Book", saved.getNameEn());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBookByName_shouldThrow_whenNotFound() {
        when(bookRepository.findByNameEn("X")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.updateBookByName("X", new BookDTO()));
    }

    @Test
    void updateBookByName_shouldSave_whenFound() {
        Book existing = new Book();
        existing.setId(1L);
        existing.setNameEn("Old");
        when(bookRepository.findByNameEn("Old")).thenReturn(Optional.of(existing));

        BookDTO dto = sampleDto(null, "NewName");

        bookService.updateBookByName("Old", dto);

        verify(bookRepository).save(existing);
        assertEquals("NewName", existing.getNameEn());
    }

    @Test
    void simpleRepoDelegations_shouldCallRepo() {
        when(bookRepository.findAllAuthors()).thenReturn(List.of("A", "B"));
        when(bookRepository.findAllLanguages()).thenReturn(List.of("ENGLISH"));
        when(bookRepository.findAllEnGenres()).thenReturn(List.of("Fantasy"));

        assertEquals(2, bookService.findAllAuthors().size());
        assertEquals(1, bookService.findAllLanguages().size());
        assertEquals(1, bookService.getAllEnGenres().size());

        verify(bookRepository).findAllAuthors();
        verify(bookRepository).findAllLanguages();
        verify(bookRepository).findAllEnGenres();
    }

    private static BookDTO sampleDto(Long id, String nameEn) {
        BookDTO dto = new BookDTO();
        dto.setId(id);
        dto.setNameEn(nameEn);
        dto.setNameUk("UA");
        dto.setAuthorEn("Auth");
        dto.setAuthorUk("AuthUA");
        dto.setDescriptionEn("Desc");
        dto.setDescriptionUk("DescUA");
        dto.setGenreEn("Fantasy");
        dto.setGenreUk("Фентезі");
        dto.setAgeGroup(AgeGroup.TEEN);
        dto.setLanguage(Language.ENGLISH);
        dto.setPublicationDate(LocalDate.of(2020, 1, 1));
        dto.setPages(100);
        dto.setPrice(new BigDecimal("12.34"));
        dto.setStockCount(5);
        return dto;
    }
}