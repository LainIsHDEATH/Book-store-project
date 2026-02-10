package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping({"/", "/home", "/index"})
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Genre genre,
                        @RequestParam(required = false) AgeGroup ageGroup,
                        @RequestParam(required = false) Language language,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "name") String sort,
                        Model model) {
        model.addAttribute("booksPage", bookService.search(keyword, genre, ageGroup, language, page, sort));
        model.addAttribute("genres", Genre.values());
        model.addAttribute("ageGroups", AgeGroup.values());
        model.addAttribute("languages", Language.values());
        model.addAttribute("sort", sort);
        return "index";
    }
}
