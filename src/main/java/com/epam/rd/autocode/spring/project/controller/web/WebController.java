package com.epam.rd.autocode.spring.project.controller.web;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import com.epam.rd.autocode.spring.project.service.auth.AuthService;
import com.epam.rd.autocode.spring.project.service.web.CartService;
import com.epam.rd.autocode.spring.project.repo.auth.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final BookService bookService;
    private final CartService cartService;
    private final OrderService orderService;
    private final AuthService authService;
    private final AuthUserRepository authUserRepository;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String genre,
                       @RequestParam(required = false) Language language,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "name") String sort,
                       Authentication authentication,
                       Model model) {
        var books = bookService.search(q, genre, language, PageRequest.of(page, 5, Sort.by(sort)));
        model.addAttribute("books", books);
        model.addAttribute("genres", bookService.getGenres());
        model.addAttribute("q", q);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedLanguage", language);
        if (authentication != null) {
            model.addAttribute("cart", cartService.get(authentication.getName()));
        }
        return "index";
    }

    @PostMapping("/cart/add/{bookName}")
    public String addToCart(@PathVariable String bookName, Authentication authentication) {
        cartService.add(authentication.getName(), bookName);
        return "redirect:/";
    }

    @PostMapping("/orders/checkout")
    public String checkout(Authentication authentication) {
        var items = cartService.get(authentication.getName()).entrySet().stream().map(e -> new BookItemDTO(e.getKey(), e.getValue())).toList();
        orderService.addOrder(new OrderDTO(authentication.getName(), null, LocalDateTime.now(), BigDecimal.ZERO, items));
        cartService.clear(authentication.getName());
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model) {
        boolean employee = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        model.addAttribute("orders", orderService.getOrdersForUser(authentication.getName(), employee));
        model.addAttribute("employee", employee);
        return "orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication authentication) {
        boolean employee = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        orderService.cancelOrder(id, authentication.getName(), employee);
        return "redirect:/orders";
    }

    @GetMapping("/employee/users")
    public String users(Model model) {
        model.addAttribute("users", authUserRepository.findAll());
        return "users";
    }

    @PostMapping("/employee/users/{email}/block")
    public String block(@PathVariable String email, @RequestParam boolean blocked) {
        authService.blockUser(email, blocked);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }
}
