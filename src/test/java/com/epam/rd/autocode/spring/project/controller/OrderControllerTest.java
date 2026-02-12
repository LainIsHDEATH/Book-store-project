package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.OrderService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock OrderService orderService;
    @InjectMocks OrderController controller;

    MockMvc mvc;

    private UsernamePasswordAuthenticationToken clientAuth() {
        return new UsernamePasswordAuthenticationToken(
                "a@a", "N/A", List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolver.redirectAware())
                .build();
    }

    @Test
    void myOrders_shouldPutOrders_andReturnView() throws Exception {
        when(orderService.getMyOrders("a@a")).thenReturn(List.of(
                OrderDTO.builder().orderId(1L).status(OrderStatus.PAID).build()
        ));

        mvc.perform(get("/orders/my").principal(clientAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/my"))
                .andExpect(model().attributeExists("orders"));

        verify(orderService).getMyOrders("a@a");
    }

    @Test
    void cancel_shouldCallService_andRedirect() throws Exception {
        doNothing().when(orderService).cancelMyOrder("a@a", 10L);

        mvc.perform(post("/orders/10/cancel").principal(clientAuth()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/my"));

        verify(orderService).cancelMyOrder("a@a", 10L);
    }

    @Test
    void viewOrder_shouldPutOrder_andReturnDetails() throws Exception {
        when(orderService.getMyOrder("a@a", 10L))
                .thenReturn(OrderDTO.builder().orderId(10L).status(OrderStatus.PAID).build());

        mvc.perform(get("/orders/10").principal(clientAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/order-details"))
                .andExpect(model().attributeExists("order"));

        verify(orderService).getMyOrder("a@a", 10L);
    }
}