package com.problemhub.controller;

import com.problemhub.model.Problem;
import com.problemhub.model.User;
import com.problemhub.service.AnalyticsService;
import com.problemhub.service.ProblemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Controller for Landing page and About page.
 */
@Controller
public class HomeController {

    private final ProblemService problemService;
    private final AnalyticsService analyticsService;

    @Autowired
    public HomeController(ProblemService problemService, AnalyticsService analyticsService) {
        this.problemService = problemService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        Integer currentUserId = (currentUser != null) ? currentUser.getId() : null;

        // Platform Statistics
        Map<String, Object> stats = analyticsService.getPlatformStats();
        model.addAttribute("stats", stats);

        // Recent 6 Problems
        List<Problem> recentProblems = problemService.getRecentProblems(6, currentUserId);
        model.addAttribute("recentProblems", recentProblems);

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About ProblemHub");
        return "about";
    }
}
