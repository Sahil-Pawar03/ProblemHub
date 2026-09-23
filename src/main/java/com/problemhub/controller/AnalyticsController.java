package com.problemhub.controller;

import com.problemhub.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Controller for public and academic platform analytics.
 */
@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public String viewAnalytics(Model model) {
        // High-level statistics
        Map<String, Object> stats = analyticsService.getPlatformStats();
        model.addAttribute("stats", stats);

        // Chart data
        Map<String, Integer> domainData = analyticsService.getProblemsByDomain();
        Map<String, Integer> difficultyData = analyticsService.getProblemsByDifficulty();
        Map<String, Integer> projectTypeData = analyticsService.getProblemsByProjectType();
        Map<String, Integer> techData = analyticsService.getPopularTechnologies();

        model.addAttribute("domainData", domainData);
        model.addAttribute("difficultyData", difficultyData);
        model.addAttribute("projectTypeData", projectTypeData);
        model.addAttribute("techData", techData);

        // Rankings
        List<Map<String, Object>> mostViewed = analyticsService.getMostViewedProblems(5);
        List<Map<String, Object>> mostSolved = analyticsService.getMostSolvedProblems(5);

        model.addAttribute("mostViewed", mostViewed);
        model.addAttribute("mostSolved", mostSolved);

        return "analytics/view";
    }
}
