package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.CartStateDTO;
import com.epam.rd.autocode.spring.project.exception.InvalidBalanceException;
import com.epam.rd.autocode.spring.project.exception.LimitException;
import com.epam.rd.autocode.spring.project.service.CartService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock CartService cartService;
    @Mock OrderService orderService;

    @InjectMocks CartController controller;

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
    void view_shouldPutCartToModel_andReturnView() throws Exception {
        when(cartService.getState("a@a")).thenReturn(new CartStateDTO(List.of(), BigDecimal.ZERO));

        mvc.perform(get("/cart").principal(clientAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/view"))
                .andExpect(model().attributeExists("cart"));

        verify(cartService).getState("a@a");
    }

    @Test
    void add_shouldRedirectToReferer_whenOk() throws Exception {
        doNothing().when(cartService).add("a@a", 7L);

        mvc.perform(post("/cart/add/7")
                        .principal(clientAuth())
                        .header("Host", "localhost:8080")
                        .header("Referer", "http://localhost:8080/?page=0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:8080/?page=0"));

        verify(cartService).add("a@a", 7L);
    }

    @Test
    void add_shouldRedirectWithStockErr_whenLimitException() throws Exception {
        doThrow(new LimitException("Stock exceeded.")).when(cartService).add("a@a", 7L);

        mvc.perform(post("/cart/add/7")
                        .principal(clientAuth())
                        .header("Host", "localhost:8080")
                        .header("Referer", "http://localhost:8080/?page=0"))
                .andExpect(status().is3xxRedirection())
                // safeBackUrlWithParams возвращает path+query, без схемы/хоста
                .andExpect(redirectedUrl("/?page=0&stockErrBookId=7"));

        verify(cartService).add("a@a", 7L);
    }

    @Test
    void minus_shouldCallService_andRedirectToReferer() throws Exception {
        doNothing().when(cartService).minus("a@a", 7L);

        mvc.perform(post("/cart/minus/7")
                        .principal(clientAuth())
                        .header("Host", "localhost:8080")
                        .header("Referer", "http://localhost:8080/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:8080/cart"));

        verify(cartService).minus("a@a", 7L);
    }

    @Test
    void remove_shouldCallService_andRedirectCart() throws Exception {
        doNothing().when(cartService).remove(100L);

        mvc.perform(post("/cart/remove/100").principal(clientAuth()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).remove(100L);
    }

    @Test
    void clear_shouldCallService_andRedirectCart() throws Exception {
        doNothing().when(cartService).clear("a@a");

        mvc.perform(post("/cart/clear").principal(clientAuth()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).clear("a@a");
    }

    @Test
    void checkout_shouldRedirectSuccess_whenOk() throws Exception {
        when(orderService.checkout("a@a")).thenReturn(null);

        mvc.perform(post("/cart/checkout").principal(clientAuth()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/my?success=true"));

        verify(orderService).checkout("a@a");
    }

    @Test
    void checkout_shouldReturnCartView_whenInsufficientBalance() throws Exception {
        when(orderService.checkout("a@a")).thenThrow(new InvalidBalanceException("no money"));
        when(cartService.getState("a@a")).thenReturn(new CartStateDTO(List.of(), new BigDecimal("10.00")));

        mvc.perform(post("/cart/checkout").principal(clientAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/view"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attribute("errorKey", "cart.error.insufficientBalance"));

        verify(orderService).checkout("a@a");
        verify(cartService).getState("a@a");
    }
}