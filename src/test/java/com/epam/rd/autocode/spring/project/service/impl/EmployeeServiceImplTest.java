package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeProfileDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock EmployeeRepository employeeRepository;

    @InjectMocks EmployeeServiceImpl employeeService;

    @Test
    void getEmployeeByEmail_shouldThrow_whenNotFound() {
        when(employeeRepository.findByEmail("a@a")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> employeeService.getEmployeeByEmail("a@a"));
    }

    @Test
    void updateProfile_shouldUpdateAndReturnDto() {
        Employee e = new Employee();
        e.setEmail("a@a");
        e.setName("Old");
        when(employeeRepository.findByEmail("a@a")).thenReturn(Optional.of(e));
        when(employeeRepository.save(e)).thenReturn(e);

        UpdateEmployeeProfileDTO dto = new UpdateEmployeeProfileDTO();
        dto.setName("New");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        dto.setPhone("123");

        EmployeeDTO res = employeeService.updateProfile("a@a", dto);

        assertEquals("a@a", res.getEmail());
        assertEquals("New", e.getName());
        assertEquals("123", e.getPhone());
        verify(employeeRepository).save(e);
    }

    @Test
    void addEmployee_shouldThrow_whenExists() {
        EmployeeDTO dto = EmployeeDTO.builder().email("a@a").build();
        when(employeeRepository.existsByEmail("a@a")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> employeeService.addEmployee(dto));
    }

    @Test
    void getAllEmployees_shouldReturnDtos() {
        Employee e = new Employee();
        e.setEmail("a@a");
        e.setName("N");
        when(employeeRepository.findAll()).thenReturn(List.of(e));

        List<EmployeeDTO> res = employeeService.getAllEmployees();

        assertEquals(1, res.size());
        assertEquals("a@a", res.get(0).getEmail());
    }
}