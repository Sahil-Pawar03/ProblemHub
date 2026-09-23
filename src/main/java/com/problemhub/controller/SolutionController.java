package com.problemhub.controller;

import com.problemhub.model.*;
import com.problemhub.service.ProblemService;
import com.problemhub.service.ProjectService;
import com.problemhub.service.SolutionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller for Solutions Knowledge Base, solution details, submission, and ratings.
 */
@Controller
@RequestMapping("/solutions")
public class SolutionController {

    private final SolutionService solutionService;
    private final ProblemService problemService;
    private final ProjectService projectService;

    @Autowired
    public SolutionController(SolutionService solutionService,
                              ProblemService problemService,
                              ProjectService projectService) {
        this.solutionService = solutionService;
        this.problemService = problemService;
        this.projectService = projectService;
    }

    @GetMapping
    public String library(@RequestParam(value = "q", required = false) String query,
                          @RequestParam(value = "domain", required = false) String domain,
                          @RequestParam(value = "tech", required = false) String tech,
                          @RequestParam(value = "year", required = false) Integer year,
                          @RequestParam(value = "difficulty", required = false) String difficulty,
                          @RequestParam(value = "type", required = false) String projectType,
                          Model model) {

        List<Solution> solutions = solutionService.searchSolutions(
                query, domain, tech, year, difficulty, projectType, "APPROVED"
        );

        model.addAttribute("solutions", solutions);
        model.addAttribute("selectedQuery", query);
        model.addAttribute("selectedDomain", domain);
        model.addAttribute("selectedTech", tech);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("selectedType", projectType);
        model.addAttribute("totalFound", solutions.size());

        return "solutions/library";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        Optional<Solution> solutionOpt = solutionService.getSolutionById(id);
        if (solutionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Solution not found.");
            return "redirect:/solutions";
        }

        Solution solution = solutionOpt.get();
        model.addAttribute("solution", solution);

        // Associated problem
        Optional<Problem> problemOpt = problemService.getProblemDetails(solution.getProblemId(), null);
        problemOpt.ifPresent(problem -> model.addAttribute("problem", problem));

        // Reviews and ratings
        List<Rating> ratings = solutionService.getSolutionRatings(id);
        model.addAttribute("ratings", ratings);

        // Check if current user already submitted a rating
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            Optional<Rating> userRating = solutionService.getUserRating(id, currentUser.getId());
            model.addAttribute("userRating", userRating.orElse(null));
        }

        return "solutions/details";
    }

    @GetMapping("/new")
    public String showSubmitForm(@RequestParam(value = "projectId", required = false) Integer projectId,
                                 @RequestParam(value = "problemId", required = false) Integer problemId,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Solution solution = new Solution();
        solution.setCollege(currentUser.getCollege() != null ? currentUser.getCollege() : "DKTE Society Textile & Engineering Institute");
        solution.setTeamNames(currentUser.getFullName());
        solution.setCompletionYear(2026);

        if (projectId != null) {
            Optional<Project> projOpt = projectService.getProjectById(projectId);
            if (projOpt.isPresent()) {
                Project p = projOpt.get();
                solution.setProjectId(p.getId());
                solution.setProblemId(p.getProblemId());
                solution.setTitle(p.getTitle());
                solution.setTechnologies(p.getTechnologiesUsed());
                solution.setTeamNames(p.getTeamMembers());
                solution.setGithubUrl(p.getRepoUrl());
                solution.setDocumentationUrl(p.getDocumentationUrl());
            }
        } else if (problemId != null) {
            solution.setProblemId(problemId);
        }

        // Available problems for selection dropdown
        List<Problem> approvedProblems = problemService.searchProblems(null, null, null, null, null, null, "APPROVED", null);
        // Student's projects
        List<Project> studentProjects = projectService.getProjectsForStudent(currentUser.getId(), "ALL");

        model.addAttribute("solution", solution);
        model.addAttribute("problems", approvedProblems);
        model.addAttribute("projects", studentProjects);

        return "solutions/submit";
    }

    @PostMapping("/submit")
    public String submitSolution(@ModelAttribute Solution solution,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        solution.setSubmittedByUserId(currentUser.getId());
        // Admin submissions auto-approved, others set to PENDING for faculty/admin review
        if (currentUser.isAdmin() || currentUser.isFaculty()) {
            solution.setStatus("APPROVED");
            solution.setVerified(true);
        } else {
            solution.setStatus("PENDING");
            solution.setVerified(false);
        }

        int newId = solutionService.submitSolution(solution);
        if (newId > 0) {
            // If linked to a project, mark the project COMPLETED and progress 100%
            if (solution.getProjectId() != null) {
                projectService.updateProgress(solution.getProjectId(), 100, "COMPLETED", solution.getGithubUrl(), solution.getDocumentationUrl());
            }

            if ("APPROVED".equalsIgnoreCase(solution.getStatus())) {
                redirectAttributes.addFlashAttribute("successMessage", "Solution published to the ProblemHub knowledge base!");
                return "redirect:/solutions/" + newId;
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Your solution will be reviewed and then added to the ProblemHub knowledge base.");
                return "redirect:/projects/my";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit solution. Please check all required fields.");
            return "redirect:/solutions/new";
        }
    }

    @PostMapping("/{id}/rate")
    public String submitRating(@PathVariable("id") int id,
                               @RequestParam("ratingValue") int ratingValue,
                               @RequestParam(value = "reviewComment", required = false) String reviewComment,
                               HttpServletRequest request,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (ratingValue < 1 || ratingValue > 5) {
            ratingValue = 5;
        }

        boolean success = solutionService.addRating(id, currentUser.getId(), ratingValue, reviewComment);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your rating and review have been submitted.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not submit rating. Please try again.");
        }

        return "redirect:/solutions/" + id;
    }
}
