package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDTO> all() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{name}")
    public BookDTO one(@PathVariable String name) {
        return bookService.getBookByName(name);
    }

    @PostMapping
    public BookDTO add(@RequestBody @Valid BookDTO dto) {
        return bookService.addBook(dto);
    }

    @PutMapping("/{name}")
    public BookDTO update(@PathVariable String name, @RequestBody @Valid BookDTO dto) {
        return bookService.updateBookByName(name, dto);
    }

    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) {
        bookService.deleteBookByName(name);
    }
}
