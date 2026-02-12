package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock RegistrationService registrationService;
    @InjectMocks RegistrationController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolver.redirectAware())
                .build();
    }

    @Test
    void registerPage_shouldReturnView_andRoles() throws Exception {
        mvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("registerRequest"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    void register_shouldCallService_andRedirectRegistered() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "ivan.test@example.com")
                        .param("name", "Ivan")
                        .param("password", "Passw0rd_123")
                        .param("confirmPassword", "Passw0rd_123")
                        .param("role", "CLIENT"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?registered=true"));

        verify(registrationService).register(any());
    }
}