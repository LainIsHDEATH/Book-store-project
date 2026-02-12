package com.epam.rd.autocode.spring.project.exception.handler;

import com.epam.rd.autocode.spring.project.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String notFound(NotFoundException ex, HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(404);
        req.setAttribute("errorKey", "error.notFound");
        req.setAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler({AlreadyExistException.class, UserAlreadyExistsException.class})
    public String conflict(RuntimeException ex, HttpServletRequest req, HttpServletResponse resp) {
        if (isPost(req)) return redirectBack(req, "/?errorKey=error.conflict", "errorKey", "error.conflict");
        resp.setStatus(409);
        req.setAttribute("errorKey", "error.conflict");
        req.setAttribute("errorMessage", ex.getMessage());
        return "error/409";
    }

    @ExceptionHandler({InvalidBalanceException.class, BalanceRechargeException.class})
    public String badBusiness(RuntimeException ex, HttpServletRequest req) {
        return redirectBack(req, "/?errorKey=error.badRequest", "errorKey", "error.badRequest");
    }

    @ExceptionHandler({TokenExpiredException.class, TokenExpirationException.class, InvalidTokenException.class, JwtAuthenticationException.class})
    public String authProblems(RuntimeException ex) {
        return "redirect:/auth/login?errorKey=auth.sessionExpired";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String denied(HttpServletResponse resp) {
        resp.setStatus(403);
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String other(Exception ex, HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(500);
        req.setAttribute("errorKey", "error.internal");
        req.setAttribute("errorMessage", ex.getMessage());
        return "error/500";
    }

    private boolean isPost(HttpServletRequest req) {
        return "POST".equalsIgnoreCase(req.getMethod());
    }

    private String redirectBack(HttpServletRequest req, String fallbackWithParams, String param, String value) {
        String ref = req.getHeader("Referer");
        if (ref == null) return "redirect:" + fallbackWithParams;

        try {
            URI uri = URI.create(ref);
            String host = uri.getHost();
            if (host != null && !host.equalsIgnoreCase(req.getServerName())) {
                return "redirect:" + fallbackWithParams;
            }

            String path = (uri.getPath() == null) ? "/" : uri.getPath();
            String query = uri.getQuery();
            String base = path + (query == null || query.isBlank() ? "" : "?" + query);

            String glue = base.contains("?") ? "&" : "?";
            return "redirect:" + base + glue + param + "=" + value;
        } catch (Exception ignore) {
            return "redirect:" + fallbackWithParams;
        }
    }
}
