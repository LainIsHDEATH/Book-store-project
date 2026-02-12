package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LogoutControllerTest {

    @Mock RefreshTokenService refreshTokenService;
    @InjectMocks LogoutController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolver.redirectAware())
                .build();
    }

    @Test
    void logout_shouldDeleteRefreshToken_whenCookiePresent() throws Exception {
        mvc.perform(post("/auth/logout")
                        .cookie(new Cookie("refresh_token", "rt-123")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(refreshTokenService).deleteByToken("rt-123");
    }

    @Test
    void logout_shouldNotCallDeleteRefreshToken_whenCookieAbsent() throws Exception {
        mvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(refreshTokenService, never()).deleteByToken(Mockito.anyString());
    }
}