package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.RefreshTokenService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock ClientService clientService;
    @Mock RefreshTokenService refreshTokenService;

    @InjectMocks ClientController controller;

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
    void showProfile_shouldReturnProfileForm() throws Exception {
        ClientDTO dto = ClientDTO.builder()
                .email("a@a")
                .name("Ivan")
                .balance(BigDecimal.ZERO)
                .build();
        when(clientService.getClientByEmail("a@a")).thenReturn(dto);

        mvc.perform(get("/clients/profile").principal(clientAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("client/profile-form"))
                .andExpect(model().attributeExists("client"))
                .andExpect(model().attributeExists("form"));

        verify(clientService).getClientByEmail("a@a");
    }

    @Test
    void saveProfile_shouldUpdateName_andRedirectSuccess() throws Exception {
        mvc.perform(post("/clients/profile/save")
                        .principal(clientAuth())
                        .param("name", "NewName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/profile?success=true"));

        verify(clientService).updateProfileName("a@a", "NewName");
    }

    @Test
    void recharge_shouldCallService_andRedirectSuccess() throws Exception {
        ClientDTO dto = ClientDTO.builder()
                .email("a@a")
                .name("Ivan")
                .balance(BigDecimal.ZERO)
                .build();
        when(clientService.getClientByEmail("a@a")).thenReturn(dto);

        mvc.perform(post("/clients/recharge")
                        .principal(clientAuth())
                        .param("amount", "10.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/recharge?success=true"));

        verify(clientService).rechargeBalanceByEmail("a@a", new BigDecimal("10.50"));
    }

    @Test
    void deleteProfile_shouldDeleteClient_deleteRefreshToken_andExpireCookies() throws Exception {
        doNothing().when(clientService).deleteProfile("a@a");
        doNothing().when(refreshTokenService).deleteByToken("rt-1");

        mvc.perform(post("/clients/profile/delete")
                        .principal(clientAuth())
                        .cookie(new Cookie("refresh_token", "rt-1")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?deleted=true"))
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(clientService).deleteProfile("a@a");
        verify(refreshTokenService).deleteByToken("rt-1");
    }
}