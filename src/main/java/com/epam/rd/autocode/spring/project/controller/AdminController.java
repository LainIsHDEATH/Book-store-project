package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.AdminService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import com.epam.rd.autocode.spring.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final EmployeeService employeeService;
    private final AdminService adminService;
    private final PasswordResetService passwordResetService;
    private final UserService userService;

    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees",
                employeeService.getAllEmployees());
        return "admin/employee-list";
    }

    @GetMapping("/employees/add")
    public String addEmployeeForm(Model model) {
        model.addAttribute("employee", new EmployeeDTO());
        return "admin/employee-form";
    }

    @GetMapping("/employees/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id,
                                   Model model) {
        model.addAttribute("employee",
                employeeService.getEmployeeById(id));
        return "admin/employee-form";
    }
}
