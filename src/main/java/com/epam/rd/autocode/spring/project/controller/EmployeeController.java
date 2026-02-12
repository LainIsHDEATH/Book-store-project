package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeProfileDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.InvalidBalanceException;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RequiredArgsConstructor
@Controller
@RequestMapping("/employees")
@PreAuthorize("hasRole('EMPLOYEE')")
@Slf4j
public class EmployeeController {

    private final BookService bookService;
    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final RefreshTokenService refreshTokenService;
//    private final LoginAttemptService loginAttemptService;
//    private final PasswordResetService passwordResetService;

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
        model.addAttribute("languages", Language.values());
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

        if (book.getId() == null) {
            bookService.addBook(book);
            return "redirect:/employees/books?created=true";
        } else {
            bookService.updateBookId(book.getId(), book);
            return "redirect:/employees/books?updated=true";
        }
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/employees/books";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        log.debug("Employee accessing order management list");
        model.addAttribute("orders", orderService.getAllOrders());
        var statuses = java.util.Arrays.stream(OrderStatus.values())
                .filter(s -> s != OrderStatus.CART)
                .toList();

        model.addAttribute("statuses", statuses);
        return "employee/order-list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getById(id)); // ownership check
        return "orders/order-details";
    }

    @PostMapping("/orders/update-status")
    public String updateStatus(@RequestParam Long orderId,
                               @RequestParam OrderStatus status,
                               Authentication auth) {

        String email = auth.getName();
        try {
            orderService.updateStatus(orderId, status, email);
        } catch (InvalidBalanceException e) {
            return "redirect:/employees/orders?errorMsg=" + e.getMessage();
        }

        return "redirect:/employees/orders";
    }

    @GetMapping("/clients")
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "employee/client-list";
    }

    @GetMapping("/clients/edit/{id}")
    public String showClientEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.getClientById(id));
        model.addAttribute("mode", "edit");
        return "employee/client-form";
    }

    @GetMapping("/clients/add")
    public String showClientAddForm(Model model) {
        log.debug("Employee opening form to create a new client");
        ClientDTO dto = new ClientDTO();
        dto.setBalance(BigDecimal.ZERO);
        model.addAttribute("client", dto);
        model.addAttribute("mode", "create");
        return "employee/client-form";
    }

    @PostMapping("/clients/save")
    public String saveClient(@Valid @ModelAttribute("client") ClientDTO clientDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", clientDto.getId() == null ? "create" : "edit");
            return "employee/client-form";
        }

        try {
            if (clientDto.getId() == null) {
                clientService.addClient(clientDto);
                return "redirect:/employees/clients?created=true";
            } else {
                clientService.updateClientById(clientDto.getId(), clientDto);
                return "redirect:/employees/clients?updated=true";
            }
        } catch (AlreadyExistException e) {
            bindingResult.rejectValue("email", "client.email.exists", e.getMessage());

            model.addAttribute("mode", "create");
            return "employee/client-form";
        }
    }

    @PostMapping("/clients/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteClientById(id);
        return "redirect:/employees/clients?deleted=true";
    }

    @PostMapping("/clients/toggle-block/{id}")
    public String toggleBlock(@PathVariable Long id) {
        clientService.toggleBlock(id);
        return "redirect:/employees/clients?updated=true";
    }

    @GetMapping("/profile")
    public String showProfile(Authentication auth, Model model) {
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);

        UpdateEmployeeProfileDTO form = new UpdateEmployeeProfileDTO();
        form.setName(employee.getName());
        form.setPhone(employee.getPhone());
        form.setBirthDate(employee.getBirthDate());

        model.addAttribute("employee", employee);
        model.addAttribute("form", form);
        return "employee/profile-form";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@Valid @ModelAttribute("form") UpdateEmployeeProfileDTO form,
                              BindingResult br,
                              Authentication auth,
                              Model model) {
        if (br.hasErrors()) {
            model.addAttribute("employee", employeeService.getEmployeeByEmail(auth.getName()));
            return "employee/profile-form";
        }

        employeeService.updateProfile(auth.getName(), form);
        return "redirect:/employees/profile?success=true";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication auth, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String email = auth.getName();

        employeeService.deleteProfile(email);

        String refresh = getCookieValue(request, "refresh_token");
        if (refresh != null) {
            refreshTokenService.deleteByToken(refresh);
        }

        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");

        SecurityContextHolder.clearContext();

        return "redirect:/login?deleted=true";
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie c = new Cookie(name, "");
        c.setPath("/");
        c.setHttpOnly(true);
        c.setMaxAge(0);
        response.addCookie(c);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

//    @PostMapping("/profile/change-password")
//    public String requestChange(Authentication auth) {
//        passwordResetService.sendResetLink(auth.getName());
//        return "redirect:/employees/profile?message=Reset+link+sent";
//    }
}
