package com.epam.rd.autocode.spring.project.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlockedController {

    @GetMapping("/blocked")
    public String blocked(HttpServletResponse resp) {
        resp.setStatus(403);
        return "error/blocked";
    }
}
