package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.CartStateDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final CartService cartService;

    @GetMapping({"/home", "/index"})
    public String home() {
        return "redirect:/";
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) AgeGroup ageGroup,
                        @RequestParam(required = false) Language language,
                        @RequestParam(required = false) String author,
                        @RequestParam(required = false) String genre,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "id_asc") String sort,
                        Authentication auth,
                        @RequestParam(required = false) Long stockErrBookId,
                        Model model) {

        keyword = blankToNull(keyword);
        author  = blankToNull(author);
        genre   = blankToNull(genre);

        Sort sortObj = Sort.by("id").ascending();

        String currentLang = LocaleContextHolder.getLocale().getLanguage();
        String nameField = "uk".equals(currentLang) ? "nameUk" : "nameEn";

        switch (sort) {
            case "price_asc" -> sortObj = Sort.by("price").ascending();
            case "price_desc" -> sortObj = Sort.by("price").descending();
            case "name_asc" -> sortObj = Sort.by(nameField).ascending();
            case "name_desc" -> sortObj = Sort.by(nameField).descending();

            case "date_asc" -> sortObj = Sort.by("publicationDate").ascending();
            case "date_desc" -> sortObj = Sort.by("publicationDate").descending();
        }

        Page<BookDTO> books = bookService.search(keyword, ageGroup, language, author, genre, page, sortObj);
        model.addAttribute("ageGroups", AgeGroup.values());
        model.addAttribute("languages", Language.values());
        model.addAttribute("allGenres", bookService.getAllGenresTranslated());

        model.addAttribute("keyword", keyword);
        model.addAttribute("ageGroup", ageGroup);

        model.addAttribute("genres", bookService.getAllEnGenres());
        model.addAttribute("genre", genre);

        model.addAttribute("language", language);
        model.addAttribute("author", author);
        model.addAttribute("sort", sort);

        boolean isClient = auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_CLIENT"::equals);

        if (isClient) {
            String email = auth.getName();
            CartStateDTO cartState = cartService.getState(email);
            List<BookItemDTO> items = cartState.getItems();
            for(BookDTO book : books.getContent()){
                for(BookItemDTO item : items){
                    if (book.getId().equals(item.getBookId())){
                        book.setCartQuantity(item.getQuantity());
                    }
                }
            }
        }
        model.addAttribute("books", books);
        model.addAttribute("stockErrBookId", stockErrBookId);

        return "index";
    }
}
