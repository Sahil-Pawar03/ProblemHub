package com.problemhub.controller;

import com.problemhub.model.Problem;
import com.problemhub.model.Project;
import com.problemhub.model.User;
import com.problemhub.service.ProblemService;
import com.problemhub.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller for student projects management, initialization, and tracking.
 */
@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProblemService problemService;

    @Autowired
    public ProjectController(ProjectService projectService, ProblemService problemService) {
        this.projectService = projectService;
        this.problemService = problemService;
    }

    @GetMapping
    public String index() {
        return "redirect:/projects/my";
    }

    @GetMapping("/my")
    public String myProjects(@RequestParam(value = "tab", defaultValue = "ALL") String tab,
                             HttpSession session,
                             Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Project> projects = projectService.getProjectsForStudent(currentUser.getId(), tab);

        // Project counts for status tabs
        int allCount = projectService.getProjectsForStudent(currentUser.getId(), "ALL").size();
        int proposedCount = projectService.getProjectsForStudent(currentUser.getId(), "PROPOSED").size();
        int inProgressCount = projectService.getProjectsForStudent(currentUser.getId(), "IN_PROGRESS").size();
        int completedCount = projectService.getProjectsForStudent(currentUser.getId(), "COMPLETED").size();

        model.addAttribute("projects", projects);
        model.addAttribute("currentTab", tab);
        model.addAttribute("allCount", allCount);
        model.addAttribute("proposedCount", proposedCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);

        return "projects/my-projects";
    }

    @GetMapping("/start/{problemId}")
    public String showStartProjectForm(@PathVariable("problemId") int problemId,
                                       HttpSession session,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login?redirect=/projects/start/" + problemId;
        }
        if (!currentUser.isStudent() && !currentUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Only students can initiate and propose projects.");
            return "redirect:/problems/" + problemId;
        }

        Optional<Problem> problemOpt = problemService.getProblemDetails(problemId, currentUser.getId());
        if (problemOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Problem statement not found.");
            return "redirect:/problems";
        }

        Problem problem = problemOpt.get();
        Project project = new Project();
        project.setProblemId(problem.getId());
        project.setTitle(problem.getTitle());
        project.setTechnologiesUsed(problem.getSuggestedTech());
        project.setTeamMembers(currentUser.getFullName());
        project.setStudentUserId(currentUser.getId());
        project.setProgressPercent(10);
        project.setStatus("PROPOSED");
        project.setStartDate(new Date(System.currentTimeMillis()));

        model.addAttribute("problem", problem);
        model.addAttribute("project", project);
        return "projects/start-project";
    }

    @PostMapping("/save")
    public String saveProject(@ModelAttribute Project project,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!currentUser.isStudent() && !currentUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Only students can register project proposals.");
            return "redirect:/problems";
        }

        project.setStudentUserId(currentUser.getId());
        project.setStatus("PROPOSED"); // Awaits faculty guide review & approval
        if (project.getProgressPercent() <= 0) {
            project.setProgressPercent(10);
        }
        if (project.getStartDate() == null) {
            project.setStartDate(new Date(System.currentTimeMillis()));
        }

        int newId = projectService.startProject(project);
        if (newId > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "Project proposal submitted successfully! Your faculty mentor will review and approve it.");
            return "redirect:/projects/my?tab=PROPOSED";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to initialize project. Please try again.");
            return "redirect:/problems/" + project.getProblemId();
        }
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable("id") int id,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Optional<Project> projOpt = projectService.getProjectById(id);
        if (projOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Project not found.");
            return "redirect:/projects/my";
        }

        Project project = projOpt.get();
        model.addAttribute("project", project);
        return "projects/details";
    }

    @PostMapping("/{id}/update-progress")
    public String updateProgress(@PathVariable("id") int id,
                                 @RequestParam("progressPercent") int progressPercent,
                                 @RequestParam("status") String status,
                                 @RequestParam(value = "repoUrl", required = false) String repoUrl,
                                 @RequestParam(value = "documentationUrl", required = false) String documentationUrl,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Project> projOpt = projectService.getProjectById(id);
        if (projOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Project not found.");
            return "redirect:/projects/my";
        }

        Project project = projOpt.get();
        // IDOR Ownership verification: Only the project owner, faculty guide, or admin can update progress
        if (project.getStudentUserId() != currentUser.getId() && !currentUser.isFaculty() && !currentUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: You can only update your own team's project.");
            return "redirect:/projects/my";
        }

        boolean success = projectService.updateProgress(id, progressPercent, status, repoUrl, documentationUrl);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Project progress updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update project progress.");
        }
        return "redirect:/projects/my";
    }
}
