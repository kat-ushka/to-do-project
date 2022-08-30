package com.github.katushka.devopswithkubernetescourse.todoproject.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/")
public class ToDoFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.endsWith("/")) {
            requestURI += "/";
        }
        httpResponse.sendRedirect(requestURI + "index.xhtml");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
