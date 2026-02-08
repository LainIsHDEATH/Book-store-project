package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final CartService cartService;

    @GetMapping({"/home", "/index"})
    public String home() {
        return "redirect:/";
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String ageGroup,
                        @RequestParam(required = false) String language,
                        @RequestParam(required = false) String author,
                        @RequestParam(required = false) String genre,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "id_asc") String sort,
                        Model model) {

        Sort sortObj = Sort.by("id").ascending();

        String currentLang = LocaleContextHolder.getLocale().getLanguage();
        String nameField = "uk".equals(currentLang) ? "nameUk" : "nameEn";

        switch (sort) {
            case "price_asc" -> sortObj = Sort.by("price").ascending();
            case "price_desc" -> sortObj = Sort.by("price").descending();
        }

        return "index";
    }
}
