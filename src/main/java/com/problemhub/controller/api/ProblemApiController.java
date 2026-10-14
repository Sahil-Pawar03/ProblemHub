package com.problemhub.controller.api;

import com.problemhub.model.ApiResponse;
import com.problemhub.model.Problem;
import com.problemhub.model.RecommendationDTO;
import com.problemhub.model.User;
import com.problemhub.service.AnalyticsService;
import com.problemhub.service.ProblemService;
import com.problemhub.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RESTful API Controller for ProblemHub.
 * Provides JSON endpoints for problems, recommendations, and platform stats.
 * Fulfills Section 4.1 & 6.4 of the project report for mobile and client consumers.
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ProblemApiController {

    private final ProblemService problemService;
    private final RecommendationService recommendationService;
    private final AnalyticsService analyticsService;

    @Autowired
    public ProblemApiController(ProblemService problemService,
                                RecommendationService recommendationService,
                                AnalyticsService analyticsService) {
        this.problemService = problemService;
        this.recommendationService = recommendationService;
        this.analyticsService = analyticsService;
    }

    /**
     * GET /api/v1/problems
     * Retrieve all approved problem statements with optional search filters.
     */
    @GetMapping("/problems")
    public ApiResponse<List<Problem>> getProblems(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "difficulty", required = false) String difficulty) {

        List<Problem> problems = problemService.searchProblems(keyword, domain, null, difficulty, null, null, "APPROVED", null);
        return ApiResponse.ok(problems, "Retrieved " + problems.size() + " problem statements");
    }

    /**
     * GET /api/v1/problems/{id}
     * Retrieve single problem statement details.
     */
    @GetMapping("/problems/{id}")
    public ResponseEntity<ApiResponse<Problem>> getProblemById(@PathVariable("id") int id) {
        Optional<Problem> problemOpt = problemService.getProblemDetails(id, null);
        if (problemOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok(problemOpt.get(), "Problem found"));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Problem statement with ID " + id + " not found"));
        }
    }

    /**
     * GET /api/v1/recommendations
     * Get Content-Based problem recommendations.
     * Works for currently logged-in student session OR accepts skills and domain via query parameters.
     */
    @GetMapping("/recommendations")
    public ApiResponse<List<RecommendationDTO>> getRecommendations(
            @RequestParam(value = "skills", required = false) String skills,
            @RequestParam(value = "interests", required = false) String interests,
            @RequestParam(value = "year", required = false, defaultValue = "3rd Year") String year,
            @RequestParam(value = "limit", defaultValue = "6") int limit,
            HttpSession session) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            user = new User();
            user.setSkills(skills != null && !skills.trim().isEmpty() ? skills : "Java, MySQL, AI");
            user.setInterests(interests != null && !interests.trim().isEmpty() ? interests : "Healthcare, Environment");
            user.setYearOfStudy(year);
        }

        List<RecommendationDTO> recommendations = recommendationService.getRecommendationsForUser(user, limit);
        return ApiResponse.ok(recommendations, "Calculated " + recommendations.size() + " Content-Based recommendations");
    }

    /**
     * GET /api/v1/stats
     * Key platform analytics numbers for dashboard consumers.
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = analyticsService.getPlatformStats();
        return ApiResponse.ok(stats, "Platform summary statistics");
    }
}
