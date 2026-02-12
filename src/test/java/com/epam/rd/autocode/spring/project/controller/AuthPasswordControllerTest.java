package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.security.JwtUtils;
import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import com.epam.rd.autocode.spring.project.service.impl.LoginAttemptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.epam.rd.autocode.spring.project.service.impl.LoginAttemptServiceImpl.LoginResult.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthPasswordControllerTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtils jwtUtils;
    @Mock RefreshTokenService refreshTokenService;
    @Mock LoginAttemptServiceImpl loginAttemptService;

    @InjectMocks AuthPasswordController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolver.redirectAware())
                .build();
    }

    @Test
    void loginPage_shouldReturnLoginView() throws Exception {
        mvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void login_shouldRedirectHome_whenOk() throws Exception {
        when(loginAttemptService.login(eq("a@a"), eq("p"), any(), any())).thenReturn(OK);

        mvc.perform(post("/auth/login")
                        .param("email", "a@a")
                        .param("password", "p"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void login_shouldRedirectBlocked_whenBlocked() throws Exception {
        when(loginAttemptService.login(eq("a@a"), eq("p"), any(), any())).thenReturn(BLOCKED);

        mvc.perform(post("/auth/login")
                        .param("email", "a@a")
                        .param("password", "p"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?blocked=true"));
    }

    @Test
    void login_shouldRedirectError_whenInvalid() throws Exception {
        when(loginAttemptService.login(eq("a@a"), eq("p"), any(), any())).thenReturn(INVALID);

        mvc.perform(post("/auth/login")
                        .param("email", "a@a")
                        .param("password", "p"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=true"));
    }
}