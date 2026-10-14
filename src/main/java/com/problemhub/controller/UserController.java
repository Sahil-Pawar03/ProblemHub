package com.problemhub.controller;

import com.problemhub.model.*;
import com.problemhub.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final SolutionService solutionService;

    @Autowired
    public UserController(UserService userService,
                          ProblemService problemService,
                          ProjectService projectService,
                          BookmarkService bookmarkService,
                          RecommendationService recommendationService,
                          SolutionService solutionService) {
        this.userService = userService;
        this.problemService = problemService;
        this.projectService = projectService;
        this.bookmarkService = bookmarkService;
        this.recommendationService = recommendationService;
        this.solutionService = solutionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "error", required = false) String error,
                            HttpSession session, Model model) {
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

        // Role-based dashboard dispatch
        if (user.isAdmin()) {
            return "redirect:/admin";
        }
        if (user.isFaculty()) {
            return "redirect:/faculty/dashboard";
        }
        if (user.isOrganization()) {
            return "redirect:/org/dashboard";
        }

        // Handle error banners
        if (error != null) {
            if ("unauthorized_admin".equals(error)) {
                model.addAttribute("errorMessage", "Access Denied: Administrator privileges required.");
            } else if ("unauthorized_faculty".equals(error)) {
                model.addAttribute("errorMessage", "Access Denied: Faculty Guide privileges required.");
            } else if ("unauthorized_org".equals(error)) {
                model.addAttribute("errorMessage", "Access Denied: Organization privileges required.");
            } else if ("unauthorized_student_only".equals(error)) {
                model.addAttribute("errorMessage", "Access Denied: That action is reserved for student accounts.");
            } else {
                model.addAttribute("errorMessage", "You are not authorized to access that section.");
            }
        }

        // Student Dashboard Metrics & Data
        int bookmarkCount = bookmarkService.getBookmarkCount(user.getId());
        int projectCount = projectService.getStudentProjectCount(user.getId());
        int completedCount = projectService.getStudentCompletedCount(user.getId());

        model.addAttribute("bookmarkCount", bookmarkCount);
        model.addAttribute("projectCount", projectCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("solutionsViewed", 12);

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

    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login?redirect=/faculty/dashboard";
        }
        if (!currentUser.isFaculty() && !currentUser.isAdmin()) {
            return "redirect:/dashboard?error=unauthorized_faculty";
        }

        // Refresh user info
        Optional<User> freshUserOpt = userService.getUserById(currentUser.getId());
        User user = freshUserOpt.orElse(currentUser);
        model.addAttribute("currentUser", user);

        // All student projects for faculty mentorship
        List<Project> allProjects = projectService.getAllProjects();
        model.addAttribute("allProjects", allProjects);
        model.addAttribute("totalProjectsGuided", allProjects.size());

        long activeCount = allProjects.stream().filter(p -> "IN_PROGRESS".equalsIgnoreCase(p.getStatus())).count();
        long completedCount = allProjects.stream().filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus())).count();
        model.addAttribute("activeProjectsCount", activeCount);
        model.addAttribute("completedProjectsCount", completedCount);

        // Solutions available for review / evaluation
        List<Solution> solutions = solutionService.searchSolutions(null, null, null, null, null, null, "APPROVED");
        model.addAttribute("solutions", solutions);
        model.addAttribute("totalSolutions", solutions.size());

        // Problems posted by faculty/college
        List<Problem> facultyProblems = problemService.getProblemsByCreatedUser(user.getId());
        model.addAttribute("facultyProblems", facultyProblems);
        model.addAttribute("totalFacultyProblems", facultyProblems.size());

        return "user/faculty-dashboard";
    }

    @PostMapping("/faculty/projects/{id}/review")
    public String reviewProject(@PathVariable("id") int id,
                                @RequestParam("status") String status,
                                @RequestParam(value = "facultyFeedback", required = false) String facultyFeedback,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || (!currentUser.isFaculty() && !currentUser.isAdmin())) {
            return "redirect:/login";
        }

        String signedFeedback = (facultyFeedback != null && !facultyFeedback.trim().isEmpty())
                ? facultyFeedback.trim() + " (Guide: " + currentUser.getFullName() + ")"
                : "Approved by " + currentUser.getFullName();

        boolean updated = projectService.updateFacultyReview(id, status, signedFeedback);
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Project #" + id + " status and guide remarks updated!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update project review.");
        }
        return "redirect:/faculty/dashboard";
    }

    @GetMapping("/org/dashboard")
    public String orgDashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login?redirect=/org/dashboard";
        }
        if (!currentUser.isOrganization() && !currentUser.isAdmin()) {
            return "redirect:/dashboard?error=unauthorized_org";
        }

        Optional<User> freshUserOpt = userService.getUserById(currentUser.getId());
        User user = freshUserOpt.orElse(currentUser);
        model.addAttribute("currentUser", user);

        List<Problem> postedProblems = problemService.getProblemsByCreatedUser(user.getId());
        model.addAttribute("postedProblems", postedProblems);
        model.addAttribute("totalPosted", postedProblems.size());
        long activeCount = postedProblems.stream().filter(p -> "APPROVED".equalsIgnoreCase(p.getStatus())).count();
        model.addAttribute("activeProblemsCount", activeCount);

        return "user/org-dashboard";
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
