package com.healthscan.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter that attaches standard HTTP defense security headers to all responses.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            // Prevent MIME type sniffing
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            // Prevent Clickjacking
            httpResponse.setHeader("X-Frame-Options", "DENY");
            // Enable browser XSS filter
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            // Control referrer information leakage
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            // Restrict sensitive browser permissions
            httpResponse.setHeader("Permissions-Policy", "camera=(self), microphone=(), geolocation=()");
        }
        chain.doFilter(request, response);
    }
}
