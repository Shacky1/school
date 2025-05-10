package com.shacv.school.controller;

import com.shacv.school.entity.Program;
import com.shacv.school.service.CustomUserDetailsService;
import com.shacv.school.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private ProgramService programService;

    // Home page mapping
    @GetMapping("/")
    public String home(Model model) {
        List<Program> programs = programService.getAllPrograms();
        model.addAttribute("programs", programs);
        return "home";
    }

    // Handle GET request for login page
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        // If there was an error, display an error message
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }

        // If the user has logged out, display a logout success message
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        return "login";
    }
}
