package com.problemhub.service;

import com.problemhub.dao.BookmarkDAO;
import com.problemhub.dao.ProblemDAO;
import com.problemhub.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for problem statement discovery, details, creation, and moderation.
 */
@Service
public class ProblemService {

    private final ProblemDAO problemDAO;
    private final BookmarkDAO bookmarkDAO;

    @Autowired
    public ProblemService(ProblemDAO problemDAO, BookmarkDAO bookmarkDAO) {
        this.problemDAO = problemDAO;
        this.bookmarkDAO = bookmarkDAO;
    }

    public Optional<Problem> getProblemDetails(int id, Integer currentUserId) {
        Optional<Problem> problemOpt = problemDAO.findById(id);
        if (problemOpt.isPresent()) {
            Problem p = problemOpt.get();
            // Increment view counter
            problemDAO.incrementViews(id);
            p.setViewsCount(p.getViewsCount() + 1);

            // Check bookmark status for logged-in user
            if (currentUserId != null) {
                p.setBookmarkedByCurrentUser(bookmarkDAO.isBookmarked(currentUserId, id));
            }
            return Optional.of(p);
        }
        return Optional.empty();
    }

    public List<Problem> searchProblems(String query, String domain, String technology,
                                        String difficulty, String duration, String projectType,
                                        String status, Integer currentUserId) {
        List<Problem> list = problemDAO.searchAndFilter(query, domain, technology, difficulty, duration, projectType, status);
        if (currentUserId != null) {
            for (Problem p : list) {
                p.setBookmarkedByCurrentUser(bookmarkDAO.isBookmarked(currentUserId, p.getId()));
            }
        }
        return list;
    }

    public List<Problem> getRecentProblems(int limit, Integer currentUserId) {
        List<Problem> list = problemDAO.findRecent(limit);
        if (currentUserId != null) {
            for (Problem p : list) {
                p.setBookmarkedByCurrentUser(bookmarkDAO.isBookmarked(currentUserId, p.getId()));
            }
        }
        return list;
    }

    public int postProblem(Problem problem) {
        return problemDAO.createProblem(problem);
    }

    public boolean updateStatus(int id, String status, boolean isVerified) {
        return problemDAO.updateStatus(id, status, isVerified);
    }

    public List<Problem> getProblemsByCreatedUser(int userId) {
        return problemDAO.findByCreatedUserId(userId);
    }

    public int getTotalCount() {
        return problemDAO.countAll();
    }

    public int getPendingCount() {
        return problemDAO.countByStatus("PENDING");
    }
}
