package dev.junaaid96.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// A Filter wraps every request: ideal for logging, auth, CORS headers. Spring Security is a chain of filters.
@WebFilter("/*")
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Work before the servlet runs.
        long start = System.nanoTime();
        // Hand over to the next filter or the target servlet.
        chain.doFilter(request, response);
        // Work after the servlet has produced its response.
        if (response instanceof HttpServletResponse http) {
            http.setHeader("X-Elapsed-Micros", String.valueOf((System.nanoTime() - start) / 1000));
        }
    }
}
