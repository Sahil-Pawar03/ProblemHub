package com.problemhub.controller;

import com.problemhub.model.Problem;
import com.problemhub.model.Solution;
import com.problemhub.model.User;
import com.problemhub.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controller for Admin moderation, verification of problems/solutions, and user management.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AnalyticsService analyticsService;
    private final ProblemService problemService;
    private final SolutionService solutionService;
    private final UserService userService;

    @Autowired
    public AdminController(AnalyticsService analyticsService,
                           ProblemService problemService,
                           SolutionService solutionService,
                           UserService userService) {
        this.analyticsService = analyticsService;
        this.problemService = problemService;
        this.solutionService = solutionService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/login";
        }

        Map<String, Object> stats = analyticsService.getPlatformStats();
        model.addAttribute("stats", stats);

        // Pending Problems
        List<Problem> pendingProblems = problemService.searchProblems(null, null, null, null, null, null, "PENDING", null);
        model.addAttribute("pendingProblems", pendingProblems);

        // Pending Solutions
        List<Solution> pendingSolutions = solutionService.searchSolutions(null, null, null, null, null, null, "PENDING");
        model.addAttribute("pendingSolutions", pendingSolutions);

        // Recent users
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "admin/dashboard";
    }

    @GetMapping("/verify-problems")
    public String verifyProblems(Model model) {
        List<Problem> allProblems = problemService.searchProblems(null, null, null, null, null, null, "ALL", null);
        model.addAttribute("problems", allProblems);
        return "admin/verify-problems";
    }

    @PostMapping("/problems/{id}/approve")
    public String approveProblem(@PathVariable("id") int id,
                                 @RequestParam(value = "redirect", required = false) String redirect,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/login";
        }
        problemService.updateStatus(id, "APPROVED", true);
        redirectAttributes.addFlashAttribute("successMessage", "Problem #" + id + " has been accepted and published to students!");
        if (redirect != null && !redirect.trim().isEmpty()) {
            return "redirect:" + redirect;
        }
        return "redirect:/admin";
    }

    @PostMapping("/problems/{id}/reject")
    public String rejectProblem(@PathVariable("id") int id,
                                @RequestParam(value = "redirect", required = false) String redirect,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/login";
        }
        problemService.updateStatus(id, "REJECTED", false);
        redirectAttributes.addFlashAttribute("infoMessage", "Problem #" + id + " has been rejected.");
        if (redirect != null && !redirect.trim().isEmpty()) {
            return "redirect:" + redirect;
        }
        return "redirect:/admin";
    }

    @GetMapping("/verify-solutions")
    public String verifySolutions(Model model) {
        List<Solution> allSolutions = solutionService.searchSolutions(null, null, null, null, null, null, "ALL");
        model.addAttribute("solutions", allSolutions);
        return "admin/verify-solutions";
    }

    @PostMapping("/solutions/{id}/approve")
    public String approveSolution(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        solutionService.updateStatus(id, "APPROVED", true);
        redirectAttributes.addFlashAttribute("successMessage", "Solution #" + id + " verified and archived into knowledge base!");
        return "redirect:/admin";
    }

    @PostMapping("/solutions/{id}/reject")
    public String rejectSolution(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        solutionService.updateStatus(id, "REJECTED", false);
        redirectAttributes.addFlashAttribute("infoMessage", "Solution #" + id + " has been rejected.");
        return "redirect:/admin";
    }
}
