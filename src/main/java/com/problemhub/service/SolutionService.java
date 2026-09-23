package com.problemhub.service;

import com.problemhub.dao.RatingDAO;
import com.problemhub.dao.SolutionDAO;
import com.problemhub.model.Rating;
import com.problemhub.model.Solution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for solution knowledge base archiving, review, and ratings.
 */
@Service
public class SolutionService {

    private final SolutionDAO solutionDAO;
    private final RatingDAO ratingDAO;

    @Autowired
    public SolutionService(SolutionDAO solutionDAO, RatingDAO ratingDAO) {
        this.solutionDAO = solutionDAO;
        this.ratingDAO = ratingDAO;
    }

    public Optional<Solution> getSolutionById(int id) {
        return solutionDAO.findById(id);
    }

    public List<Solution> getSolutionsForProblem(int problemId) {
        return solutionDAO.findByProblemId(problemId);
    }

    public List<Solution> searchSolutions(String query, String domain, String tech,
                                          Integer year, String difficulty, String projectType,
                                          String status) {
        return solutionDAO.searchAndFilter(query, domain, tech, year, difficulty, projectType, status);
    }

    public int submitSolution(Solution solution) {
        return solutionDAO.createSolution(solution);
    }

    public boolean updateStatus(int id, String status, boolean isVerified) {
        return solutionDAO.updateStatus(id, status, isVerified);
    }

    public boolean addRating(int solutionId, int userId, int ratingValue, String reviewComment) {
        return ratingDAO.addOrUpdateRating(solutionId, userId, ratingValue, reviewComment);
    }

    public List<Rating> getSolutionRatings(int solutionId) {
        return ratingDAO.findBySolutionId(solutionId);
    }

    public Optional<Rating> getUserRating(int solutionId, int userId) {
        return ratingDAO.getUserRating(solutionId, userId);
    }

    public int getTotalCount() {
        return solutionDAO.countAll();
    }

    public int getPendingCount() {
        return solutionDAO.countPending();
    }
}
