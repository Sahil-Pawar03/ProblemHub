package com.problemhub.controller;

import com.problemhub.model.User;
import com.problemhub.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller for user authentication: login, registration, logout, and viva demo switcher.
 */
@Controller
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "role", defaultValue = "STUDENT") String role,
                            @RequestParam(value = "redirect", required = false) String redirect,
                            @RequestParam(value = "error", required = false) String error,
                            Model model, HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser.isAdmin()) return "redirect:/admin";
            if (currentUser.isFaculty()) return "redirect:/faculty/dashboard";
            if (currentUser.isOrganization()) return "redirect:/org/dashboard";
            return "redirect:/dashboard";
        }
        model.addAttribute("selectedRole", role != null ? role.toUpperCase() : "STUDENT");
        model.addAttribute("redirect", redirect);
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        return "auth/login";
    }

    @PostMapping("/process-login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam(value = "role", required = false) String selectedRole,
                               @RequestParam(value = "redirect", required = false) String redirect,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = authService.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Validate selected role against actual user role
            if (selectedRole != null && !selectedRole.trim().isEmpty() && !"ANY".equalsIgnoreCase(selectedRole)) {
                if (!user.getRole().equalsIgnoreCase(selectedRole.trim())) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Role Mismatch: Your account is registered as " + user.getRole() +
                            ", but you selected " + selectedRole.toUpperCase() +
                            ". Please select the " + user.getRole() + " role tab to sign in.");
                    return "redirect:/login?role=" + user.getRole() + (redirect != null ? "&redirect=" + redirect : "");
                }
            }

            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", user.getRole());
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getFullName() + " (" + user.getRole() + ")!");

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("/login")) {
                return "redirect:" + redirect;
            }
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin";
            }
            if ("FACULTY".equalsIgnoreCase(user.getRole())) {
                return "redirect:/faculty/dashboard";
            }
            if ("ORGANIZATION".equalsIgnoreCase(user.getRole())) {
                return "redirect:/org/dashboard";
            }
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials. Please verify your email and password, or use the 1-click viva demo login below.");
            return "redirect:/login" + (selectedRole != null ? "?role=" + selectedRole : "") + (redirect != null ? "&redirect=" + redirect : "");
        }
    }

    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/process-register")
    public String processRegister(@RequestParam("fullName") String fullName,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  @RequestParam("role") String role,
                                  @RequestParam(value = "college", required = false) String college,
                                  @RequestParam(value = "department", required = false) String department,
                                  @RequestParam(value = "yearOfStudy", required = false) String yearOfStudy,
                                  @RequestParam(value = "skills", required = false) String skills,
                                  @RequestParam(value = "interests", required = false) String interests,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/register";
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 6 characters.");
            return "redirect:/register";
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email.trim().toLowerCase());
        user.setRole(role);
        user.setCollege(college);
        user.setDepartment(department);
        user.setYearOfStudy(yearOfStudy);
        user.setSkills(skills);
        user.setInterests(interests);

        boolean success = authService.register(user, password);
        if (success) {
            // Auto login after registration
            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", user.getRole());
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Welcome to ProblemHub.");
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Email is already registered. Please login.");
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("infoMessage", "You have been logged out successfully.");
        return "redirect:/login";
    }

    /**
     * Viva Demo Quick-Switcher:
     * Allows examiners/presenters to instantly log in as predefined demo roles with a single click.
     */
    @GetMapping("/demo-login")
    public String demoLogin(@RequestParam("role") String role, HttpSession session, RedirectAttributes redirectAttributes) {
        String email;
        switch (role.toUpperCase()) {
            case "ADMIN":
                email = "admin@problemhub.com";
                break;
            case "STUDENT":
                email = "rahul.student@problemhub.com";
                break;
            case "FACULTY":
                email = "prof.sharma@problemhub.com";
                break;
            case "ORGANIZATION":
                email = "contact@cleanearthngo.org";
                break;
            default:
                email = "rahul.student@problemhub.com";
        }

        Optional<User> userOpt = authService.authenticate(email, "password123");
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", user.getRole());
            redirectAttributes.addFlashAttribute("successMessage", "Demo session started as " + user.getFullName() + " (" + user.getRole() + ")");
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin";
            }
            if ("FACULTY".equalsIgnoreCase(user.getRole())) {
                return "redirect:/faculty/dashboard";
            }
            if ("ORGANIZATION".equalsIgnoreCase(user.getRole())) {
                return "redirect:/org/dashboard";
            }
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Could not start demo session.");
        return "redirect:/login";
    }
}
