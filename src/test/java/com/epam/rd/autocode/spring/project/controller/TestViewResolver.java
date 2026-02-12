package com.epam.rd.autocode.spring.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Locale;
import java.util.Map;

public final class TestViewResolver {

    private TestViewResolver() {}

    public static ViewResolver redirectAware() {
        return (String viewName, Locale locale) -> {
            if (viewName == null) return null;

            if (viewName.startsWith("redirect:")) {
                String target = viewName.substring("redirect:".length());
                return new RedirectView(target, true);
            }

            if (viewName.startsWith("forward:")) {
                String target = viewName.substring("forward:".length());
                return new InternalResourceView(target);
            }

            return new AbstractView() {
                @Override
                protected void renderMergedOutputModel(
                        Map<String, Object> model,
                        HttpServletRequest request,
                        HttpServletResponse response
                ) {
                }
            };
        };
    }
}