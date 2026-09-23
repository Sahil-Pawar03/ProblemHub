package com.problemhub.controller;

import com.problemhub.model.Bookmark;
import com.problemhub.model.User;
import com.problemhub.service.BookmarkService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for student bookmarked problems.
 */
@Controller
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public String listBookmarks(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Bookmark> bookmarks = bookmarkService.getUserBookmarks(currentUser.getId());
        model.addAttribute("bookmarks", bookmarks);
        return "bookmarks/list";
    }

    @PostMapping("/remove/{problemId}")
    public String removeBookmark(@PathVariable("problemId") int problemId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        bookmarkService.removeBookmark(currentUser.getId(), problemId);
        redirectAttributes.addFlashAttribute("infoMessage", "Problem removed from bookmarks.");
        return "redirect:/bookmarks";
    }
}
