package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.InvalidBalanceException;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock BookService bookService;
    @Mock OrderService orderService;
    @Mock ClientService clientService;
    @Mock EmployeeService employeeService;
    @Mock RefreshTokenService refreshTokenService;

    @InjectMocks EmployeeController controller;

    MockMvc mvc;

    private UsernamePasswordAuthenticationToken employeeAuth() {
        return new UsernamePasswordAuthenticationToken(
                "e@e", "N/A", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );
    }

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolver.redirectAware())
                .build();
    }

    @Test
    void listOrders_shouldReturnView_andStatusesWithoutCart() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(
                OrderDTO.builder().orderId(1L).status(OrderStatus.PAID).build()
        ));

        mvc.perform(get("/employees/orders").principal(employeeAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/order-list"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attribute("statuses", not(hasItem(OrderStatus.CART))));
    }

    @Test
    void updateStatus_shouldCallService_andRedirect() throws Exception {
        doNothing().when(orderService).updateStatus(1L, OrderStatus.SHIPPED, "e@e");

        mvc.perform(post("/employees/orders/update-status")
                        .principal(employeeAuth())
                        .param("orderId", "1")
                        .param("status", "SHIPPED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees/orders"));

        verify(orderService).updateStatus(1L, OrderStatus.SHIPPED, "e@e");
    }

    @Test
    void updateStatus_shouldRedirectWithErrorMsg_whenInvalidBalance() throws Exception {
        doThrow(new InvalidBalanceException("bad")).when(orderService)
                .updateStatus(1L, OrderStatus.SHIPPED, "e@e");

        mvc.perform(post("/employees/orders/update-status")
                        .principal(employeeAuth())
                        .param("orderId", "1")
                        .param("status", "SHIPPED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees/orders?errorMsg=bad"));
    }
}