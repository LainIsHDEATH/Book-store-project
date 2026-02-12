package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.CartStateDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock BookService bookService;
    @Mock CartService cartService;

    @InjectMocks HomeController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolver.redirectAware())
                .build();
    }

    @Test
    void index_shouldReturnIndex_andPutBooksToModel() throws Exception {
        BookDTO b = new BookDTO();
        b.setId(1L);

        Page<BookDTO> page = new PageImpl<>(List.of(b), PageRequest.of(0, 6), 1);

        when(bookService.search(any(), any(), any(), any(), any(), eq(0), any(Sort.class))).thenReturn(page);
        when(bookService.getAllGenresTranslated()).thenReturn(List.of());
        when(bookService.getAllEnGenres()).thenReturn(List.of("Adventure"));

        mvc.perform(get("/")
                        .param("page", "0")
                        .param("sort", "id_asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("ageGroups"))
                .andExpect(model().attributeExists("languages"))
                .andExpect(model().attributeExists("allGenres"));
    }
}