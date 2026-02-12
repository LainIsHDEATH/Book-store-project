package com.epam.rd.autocode.spring.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BlockedControllerTest {

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new BlockedController())
                .setViewResolvers(TestViewResolver.redirectAware())
                .build();
    }

    @Test
    void blocked_shouldReturn403_andView() throws Exception {
        mvc.perform(get("/blocked"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/blocked"));
    }
}