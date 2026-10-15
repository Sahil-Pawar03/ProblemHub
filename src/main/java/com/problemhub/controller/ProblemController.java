package com.problemhub.controller;

import com.problemhub.model.Problem;
import com.problemhub.model.Solution;
import com.problemhub.model.User;
import com.problemhub.service.BookmarkService;
import com.problemhub.service.ProblemService;
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
 * Controller for problem exploration, details, posting, and bookmarking.
 */
@Controller
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final SolutionService solutionService;
    private final BookmarkService bookmarkService;

    @Autowired
    public ProblemController(ProblemService problemService,
                             SolutionService solutionService,
                             BookmarkService bookmarkService) {
        this.problemService = problemService;
        this.solutionService = solutionService;
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public String explore(@RequestParam(value = "q", required = false) String query,
                          @RequestParam(value = "domain", required = false) String domain,
                          @RequestParam(value = "tech", required = false) String tech,
                          @RequestParam(value = "difficulty", required = false) String difficulty,
                          @RequestParam(value = "duration", required = false) String duration,
                          @RequestParam(value = "type", required = false) String projectType,
                          @RequestParam(value = "error", required = false) String error,
                          HttpSession session,
                          Model model) {

        User currentUser = (User) session.getAttribute("currentUser");
        Integer currentUserId = (currentUser != null) ? currentUser.getId() : null;

        if ("unauthorized_post_problem".equals(error)) {
            model.addAttribute("errorMessage", "Access Denied: Problem statements are submitted by Partner Organizations and Faculty Mentors. Students can explore challenges and register project proposals.");
        }

        List<Problem> problems = problemService.searchProblems(
                query, domain, tech, difficulty, duration, projectType, "APPROVED", currentUserId
        );

        model.addAttribute("problems", problems);
        model.addAttribute("selectedQuery", query);
        model.addAttribute("selectedDomain", domain);
        model.addAttribute("selectedTech", tech);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("selectedDuration", duration);
        model.addAttribute("selectedType", projectType);
        model.addAttribute("totalFound", problems.size());

        return "problems/explore";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") int id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        Integer currentUserId = (currentUser != null) ? currentUser.getId() : null;

        Optional<Problem> problemOpt = problemService.getProblemDetails(id, currentUserId);
        if (problemOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Problem statement not found.");
            return "redirect:/problems";
        }

        Problem problem = problemOpt.get();

        // If problem is not approved, only the author or an administrator should see it
        if (!"APPROVED".equalsIgnoreCase(problem.getStatus())) {
            boolean isAuthor = (currentUserId != null && problem.getCreatedByUserId() != null && problem.getCreatedByUserId().equals(currentUserId));
            boolean isAdmin = (currentUser != null && currentUser.isAdmin());
            if (!isAuthor && !isAdmin) {
                redirectAttributes.addFlashAttribute("errorMessage", "This problem statement is currently pending administrator verification.");
                return "redirect:/problems";
            }
        }

        model.addAttribute("problem", problem);

        // Fetch previous student solutions for this problem statement
        List<Solution> previousSolutions = solutionService.getSolutionsForProblem(id);
        model.addAttribute("previousSolutions", previousSolutions);

        return "problems/details";
    }

    @GetMapping("/new")
    public String showPostForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login?redirect=/problems/new";
        }
        if (currentUser.isStudent()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Problem statements are submitted by Partner Organizations and Faculty Mentors. Students can explore challenges and register project proposals.");
            return "redirect:/problems";
        }

        Problem p = new Problem();
        if (currentUser.isOrganization()) {
            p.setOrganizationName(currentUser.getFullName());
            p.setContactInfo(currentUser.getEmail());
            p.setSourceType("Industry");
        } else if (currentUser.isFaculty()) {
            p.setOrganizationName(currentUser.getCollege() != null ? currentUser.getCollege() : "Academic Institution");
            p.setContactInfo(currentUser.getEmail());
            p.setSourceType("Academic");
        }
        model.addAttribute("problem", p);
        return "problems/post";
    }

    @PostMapping("/post")
    public String postProblem(@ModelAttribute Problem problem,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (currentUser.isStudent()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Problem statements are submitted by Partner Organizations and Faculty Mentors. Students can explore challenges and register project proposals.");
            return "redirect:/problems";
        }

        problem.setCreatedByUserId(currentUser.getId());
        // Only Admin gets auto-approved. Organizations and Faculty submissions must be PENDING for admin review/acceptance!
        if (currentUser.isAdmin()) {
            problem.setStatus("APPROVED");
            problem.setVerified(true);
        } else {
            problem.setStatus("PENDING");
            problem.setVerified(false);
        }

        int newId = problemService.postProblem(problem);
        if (newId > 0) {
            if ("APPROVED".equalsIgnoreCase(problem.getStatus())) {
                redirectAttributes.addFlashAttribute("successMessage", "Problem statement published successfully!");
                return "redirect:/problems/" + newId;
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Problem statement submitted successfully! It is now pending administrator review and will appear in the catalogue once accepted.");
                if (currentUser.isOrganization()) {
                    return "redirect:/org/dashboard";
                } else if (currentUser.isFaculty()) {
                    return "redirect:/faculty/dashboard";
                } else {
                    return "redirect:/problems";
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit problem. Please check required fields.");
            return "redirect:/problems/new";
        }
    }

    @PostMapping("/{id}/bookmark")
    public String toggleBookmark(@PathVariable("id") int id,
                                 HttpServletRequest request,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        boolean isBookmarked = bookmarkService.toggleBookmark(currentUser.getId(), id);
        if (isBookmarked) {
            redirectAttributes.addFlashAttribute("successMessage", "Problem added to your bookmarks!");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "Problem removed from bookmarks.");
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/problems/" + id);
    }
}
