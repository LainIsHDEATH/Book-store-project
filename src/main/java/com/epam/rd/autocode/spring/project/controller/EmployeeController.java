package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.PasswordChangeDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeController {

    private final BookService bookService;
    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAll());
        model.addAttribute("statuses", OrderStatus.values());
        return "employee/order-list";
    }

    @PostMapping("/orders/update-status")
    public String updateStatus(@RequestParam Long orderId, @RequestParam OrderStatus status, Principal principal) {
        orderService.updateStatus(orderId, status, principal.getName());
        return "redirect:/employee/orders";
    }

    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("booksPage", bookService.search(null, null, null, null, 0, "name"));
        return "employee/book-list";
    }

    @GetMapping("/books/add")
    public String addBookPage(Model model) {
        model.addAttribute("book", new BookDTO());
        addBookMeta(model);
        return "employee/book-form";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookPage(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getById(id));
        addBookMeta(model);
        return "employee/book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid @ModelAttribute("book") BookDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            addBookMeta(model);
            return "employee/book-form";
        }
        bookService.save(dto);
        return "redirect:/employee/books";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/employee/books";
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("clients", clientService.getAll());
        return "employee/client-list";
    }

    @GetMapping("/clients/toggle-block/{id}")
    public String toggleBlock(@PathVariable Long id) {
        clientService.toggleBlock(id);
        return "redirect:/employee/clients";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("employee", employeeService.getByEmail(principal.getName()));
        model.addAttribute("passwordChange", new PasswordChangeDTO());
        return "employee/profile-form";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@Valid @ModelAttribute("employee") EmployeeDTO dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) return "employee/profile-form";
        employeeService.updateProfile(principal.getName(), dto);
        return "redirect:/employee/profile?success=true";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute("passwordChange") PasswordChangeDTO dto, Principal principal) {
        employeeService.changePassword(principal.getName(), dto.getOldPassword(), dto.getNewPassword());
        return "redirect:/employee/profile?pwdSuccess=true";
    }

    private void addBookMeta(Model model) {
        model.addAttribute("genres", Genre.values());
        model.addAttribute("ageGroups", AgeGroup.values());
        model.addAttribute("languages", Language.values());
    }
}
