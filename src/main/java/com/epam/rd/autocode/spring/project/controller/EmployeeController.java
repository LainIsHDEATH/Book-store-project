package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAuthority('EMPLOYEE')")
@Slf4j
public class EmployeeController {

    private final BookService bookService;
    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final LoginAttemptService loginAttemptService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @GetMapping("/books")
    public String listBooks(Model model) {
        log.debug("Employee accessing book management list");
        model.addAttribute("books", bookService.getAllBooks());
        return "employee/book-list";
    }

    @GetMapping("/books/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new BookDTO());
        addFormAttributes(model);
        return "employee/book-form";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        addFormAttributes(model);
        return "employee/book-form";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("ageGroups", AgeGroup.values());
        model.addAttribute("languages", bookService.getAllLanguages());
        model.addAttribute("allGenres", bookService.getAllGenresTranslated());
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid @ModelAttribute("book") BookDTO book,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            log.error("Validation errors while saving book: {}", bindingResult.getAllErrors());
            addFormAttributes(model);
            return "employee/book-form";
        }

        if (book.getId() != null) {
            bookService.updateBookByName(book.getNameEn(), book);
        } else {
            bookService.addBook(book);
        }

        return "redirect:/employee/books";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/employee/books";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        log.debug("Employee accessing order management list");
        model.addAttribute("orders", orderService.getAllOrders());
        return "employee/order-list";
    }

    @PostMapping("/orders/update-status")
    public String updateStatus(@RequestParam Long orderId,
                               @RequestParam OrderStatus status,
                               Principal principal) {

        try {
            orderService.updateStatus(orderId, status, principal.getName());
        } catch (InvalidBalanceException e) {
            return "redirect:/employee/orders?errorMsg=" + e.getMessage();
        }

        return "redirect:/employee/orders";
    }

    @GetMapping("/clients")
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "employee/client-list";
    }

    @GetMapping("/clients/edit/{id}")
    public String showClientEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.getClientById(id));
        return "employee/client-form";
    }

    @GetMapping("/clients/add")
    public String showClientAddForm(Model model) {
        log.debug("Employee opening form to create a new client");
        model.addAttribute("client", new ClientDTO());
        return "employee/client-form";
    }

    @PostMapping("/clients/save")
    public String saveClient(@Valid @ModelAttribute("client") ClientDTO clientDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "employee/client-form";
        }

        clientService.saveClient(clientDto);
        return "redirect:/employee/clients";
    }

    @GetMapping("/clients/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return "redirect:/employee/clients";
    }

    @PostMapping("/clients/unlock")
    public String unlockClient(@RequestParam String email) {
        loginAttemptService.unlockUser(email);
        return "redirect:/employee/clients?message=Client_unlocked&email=" + email;
    }

    @GetMapping("/clients/toggle-block/{id}")
    public String toggleBlockClient(@PathVariable Long id) {
        userService.toggleBlockClient(id);
        return "redirect:/employee/clients";
    }

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        String email = principal.getName();
        model.addAttribute("employee",
                employeeService.getEmployeeByEmail(email));
        return "employee/profile-form";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@Valid @ModelAttribute("employee") EmployeeDTO dto,
                              BindingResult bindingResult,
                              Principal principal) {

        if (bindingResult.hasErrors()) {
            return "employee/profile-form";
        }

        employeeService.updateProfile(principal.getName(), dto);
        return "redirect:/employee/profile?success=true";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Principal principal, HttpServletRequest request) throws ServletException {
        String email = principal.getName();

        if (email.contains("@")) {
            employeeService.deleteProfile(email);
        }

        request.logout();

        return "redirect:/login?deleted=true";
    }

    @PostMapping("/profile/change-password")
    public String requestChange(Principal principal) {
        passwordResetService.sendResetLink(principal.getName());
        return "redirect:/employee/profile?message=Reset+link+sent";
    }

//    @GetMapping
//    public List<EmployeeDTO> all() {
//        return employeeService.getAllEmployees();
//    }

//    @GetMapping("/{email}")
//    public EmployeeDTO one(@PathVariable String email) {
//        return employeeService.getEmployeeByEmail(email);
//    }

//    @PostMapping
//    public EmployeeDTO add(@RequestBody @Valid EmployeeDTO dto) {
//        return employeeService.addEmployee(dto);
//    }

//    @PutMapping("/{email}")
//    public EmployeeDTO update(@PathVariable String email, @RequestBody @Valid EmployeeDTO dto) {
//        return employeeService.updateEmployeeByEmail(email, dto);
//    }

//    @DeleteMapping("/{email}")
//    public void delete(@PathVariable String email) {
//        employeeService.deleteEmployeeByEmail(email);
//    }
}
