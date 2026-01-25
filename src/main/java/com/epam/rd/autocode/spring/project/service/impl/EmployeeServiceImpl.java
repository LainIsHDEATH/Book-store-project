package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream().map(EmployeeServiceImpl::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee e = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + email));
        return toDto(e);
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee) {
        Employee e = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + email));

        e.setEmail(employee.getEmail());
        e.setPassword(employee.getPassword());
        e.setName(employee.getName());
        e.setBirthDate(employee.getBirthDate());
        e.setPhone(employee.getPhone());

        return toDto(employeeRepository.save(e));
    }

    @Override
    public void deleteEmployeeByEmail(String email) {
        Employee e = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + email));
        employeeRepository.delete(e);
    }

    @Override
    public EmployeeDTO addEmployee(EmployeeDTO employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new AlreadyExistException("Employee already exists: " + employee.getEmail());
        }
        Employee saved = employeeRepository.save(toEntity(employee));
        return toDto(saved);
    }

    private static EmployeeDTO toDto(Employee e) {
        return new EmployeeDTO(e.getEmail(), e.getPassword(), e.getName(), e.getBirthDate(), e.getPhone());
    }

    private static Employee toEntity(EmployeeDTO d) {
        return new Employee(null, d.getEmail(), d.getPassword(), d.getName(), d.getBirthDate(), d.getPhone());
    }
}
