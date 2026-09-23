package com.problemhub.controller;

import com.problemhub.model.*;
import com.problemhub.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller for User Dashboards (Student, Organization, Faculty) and Profile management.
 */
@Controller
public class UserController {

    private final UserService userService;
    private final ProblemService problemService;
    private final ProjectService projectService;
    private final BookmarkService bookmarkService;
    private final RecommendationService recommendationService;

    @Autowired
    public UserController(UserService userService,
                          ProblemService problemService,
                          ProjectService projectService,
                          BookmarkService bookmarkService,
                          RecommendationService recommendationService) {
        this.userService = userService;
        this.problemService = problemService;
        this.projectService = projectService;
        this.bookmarkService = bookmarkService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Refresh user info from database
        Optional<User> freshUserOpt = userService.getUserById(currentUser.getId());
        freshUserOpt.ifPresent(fresh -> {
            session.setAttribute("currentUser", fresh);
            model.addAttribute("currentUser", fresh);
        });
        User user = freshUserOpt.orElse(currentUser);

        // If Admin, redirect to admin dashboard
        if (user.isAdmin()) {
            return "redirect:/admin";
        }

        // If Organization, show Organization Dashboard
        if (user.isOrganization()) {
            List<Problem> postedProblems = problemService.getProblemsByCreatedUser(user.getId());
            model.addAttribute("postedProblems", postedProblems);
            model.addAttribute("totalPosted", postedProblems.size());
            long activeCount = postedProblems.stream().filter(p -> "APPROVED".equalsIgnoreCase(p.getStatus())).count();
            model.addAttribute("activeProblemsCount", activeCount);
            return "user/org-dashboard";
        }

        // Student & Faculty Dashboard
        int bookmarkCount = bookmarkService.getBookmarkCount(user.getId());
        int projectCount = projectService.getStudentProjectCount(user.getId());
        int completedCount = projectService.getStudentCompletedCount(user.getId());

        model.addAttribute("bookmarkCount", bookmarkCount);
        model.addAttribute("projectCount", projectCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("solutionsViewed", 12); // Sample counter

        // Active Projects
        List<Project> activeProjects = projectService.getProjectsForStudent(user.getId(), "IN_PROGRESS");
        model.addAttribute("activeProjects", activeProjects);

        // Score-Based Explainable Recommendations
        List<RecommendationDTO> recommendations = recommendationService.getRecommendationsForUser(user, 4);
        model.addAttribute("recommendations", recommendations);

        // Recently Added Problems
        List<Problem> recentProblems = problemService.getRecentProblems(4, user.getId());
        model.addAttribute("recentProblems", recentProblems);

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<User> freshUserOpt = userService.getUserById(currentUser.getId());
        User user = freshUserOpt.orElse(currentUser);
        model.addAttribute("user", user);

        int completedProjects = projectService.getStudentCompletedCount(user.getId());
        int activeProjects = projectService.getStudentProjectCount(user.getId()) - completedProjects;
        model.addAttribute("completedProjects", completedProjects);
        model.addAttribute("activeProjects", activeProjects);

        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam(value = "college", required = false) String college,
                                @RequestParam(value = "department", required = false) String department,
                                @RequestParam(value = "yearOfStudy", required = false) String yearOfStudy,
                                @RequestParam(value = "skills", required = false) String skills,
                                @RequestParam(value = "interests", required = false) String interests,
                                @RequestParam(value = "bio", required = false) String bio,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        currentUser.setFullName(fullName);
        currentUser.setCollege(college);
        currentUser.setDepartment(department);
        currentUser.setYearOfStudy(yearOfStudy);
        currentUser.setSkills(skills);
        currentUser.setInterests(interests);
        currentUser.setBio(bio);

        boolean updated = userService.updateProfile(currentUser);
        if (updated) {
            session.setAttribute("currentUser", currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile.");
        }

        return "redirect:/profile";
    }
}
