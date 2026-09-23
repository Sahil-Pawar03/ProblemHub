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

        // Static resources and public routes are always allowed
        if (uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images") ||
            uri.equals("/") || uri.equals("/about") || uri.equals("/login") || uri.equals("/register") ||
            uri.equals("/process-login") || uri.equals("/process-register") || uri.equals("/logout") || uri.startsWith("/demo-login") ||
            uri.equals("/problems") || uri.equals("/solutions") || uri.equals("/analytics") ||
            (uri.startsWith("/problems/") && !uri.equals("/problems/new") && !uri.endsWith("/bookmark")) ||
            (uri.startsWith("/solutions/") && !uri.equals("/solutions/new") && !uri.endsWith("/rate"))) {
            return true;
        }

        // Check if user is logged in
        if (currentUser == null) {
            String redirectUrl = "/login?redirect=" + java.net.URLEncoder.encode(uri, "UTF-8");
            response.sendRedirect(redirectUrl);
            return false;
        }

        // Check admin-only routes
        if (uri.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("/dashboard?error=unauthorized");
            return false;
        }

        return true;
    }
}
