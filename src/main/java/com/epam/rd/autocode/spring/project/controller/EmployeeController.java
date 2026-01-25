package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeDTO> all() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{email}")
    public EmployeeDTO one(@PathVariable String email) {
        return employeeService.getEmployeeByEmail(email);
    }

    @PostMapping
    public EmployeeDTO add(@RequestBody @Valid EmployeeDTO dto) {
        return employeeService.addEmployee(dto);
    }

    @PutMapping("/{email}")
    public EmployeeDTO update(@PathVariable String email, @RequestBody @Valid EmployeeDTO dto) {
        return employeeService.updateEmployeeByEmail(email, dto);
    }

    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        employeeService.deleteEmployeeByEmail(email);
    }
}
