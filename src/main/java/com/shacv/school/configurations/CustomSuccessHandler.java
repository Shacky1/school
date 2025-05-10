package com.shacv.school.configurations;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        var authorities = authentication.getAuthorities();
        Optional<String> role = authorities.stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst();

        // Determine the redirect URL based on the role
        if (role.isPresent()) {
            switch (role.get()) {
                case "ADMIN":
                    response.sendRedirect("/admin_page");
                    break;
                case "MANAGER":
                    response.sendRedirect("/manager/dashboard");
                    break;
                case "HOD":
                    response.sendRedirect("/hod/dashboard");
                    break;
                case "STUDENT": // Assuming you have a USER role, otherwise replace with your user role representation
                    response.sendRedirect("/student/dashboard");
                    break;
                case "TEACHER":
                    response.sendRedirect("/teacher/dashboard");
                    break;
                default:
                    response.sendRedirect("/user_page"); // Redirect for any other roles
                    break;
            }
        } else {
            // Fallback if no roles found - could also log an error or redirect to an error page
            response.sendRedirect("/");
        }
    }
}
