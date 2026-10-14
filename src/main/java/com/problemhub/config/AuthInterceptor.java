package com.problemhub.config;

import com.problemhub.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for session authentication and role-based route protection.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Static resources and public REST API endpoints are always allowed
        if (uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images") || uri.equals("/favicon.ico") || uri.startsWith("/api/")) {
            return true;
        }

        String method = request.getMethod();

        // Public GET pages
        if ("GET".equalsIgnoreCase(method)) {
            if (uri.equals("/") || uri.equals("/about") || uri.equals("/login") || uri.equals("/register") ||
                uri.equals("/logout") || uri.startsWith("/demo-login") ||
                uri.equals("/problems") || uri.equals("/solutions") || uri.equals("/analytics") || uri.equals("/api-docs") ||
                uri.matches("^/problems/\\d+$") || uri.matches("^/solutions/\\d+$")) {
                return true;
            }
        }

        // Public POST auth endpoints
        if ("POST".equalsIgnoreCase(method)) {
            if (uri.equals("/process-login") || uri.equals("/process-register")) {
                return true;
            }
        }

        // Check if user is logged in
        if (currentUser == null) {
            String redirectUrl = "/login?redirect=" + java.net.URLEncoder.encode(uri, "UTF-8");
            response.sendRedirect(redirectUrl);
            return false;
        }

        // Role-based route protection:
        // 1. Admin-only routes
        if (uri.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("/dashboard?error=unauthorized_admin");
            return false;
        }

        // 2. Faculty-only routes
        if (uri.startsWith("/faculty") && !currentUser.isFaculty() && !currentUser.isAdmin()) {
            response.sendRedirect("/dashboard?error=unauthorized_faculty");
            return false;
        }

        // 3. Organization-only routes
        if (uri.startsWith("/org") && !currentUser.isOrganization() && !currentUser.isAdmin()) {
            response.sendRedirect("/dashboard?error=unauthorized_org");
            return false;
        }

        return true;
    }
}
