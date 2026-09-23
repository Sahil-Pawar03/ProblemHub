package com.problemhub.service;

import com.problemhub.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service providing statistical rollups and Chart.js datasets.
 */
@Service
public class AnalyticsService {

    private final AnalyticsDAO analyticsDAO;
    private final ProblemDAO problemDAO;
    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;
    private final SolutionDAO solutionDAO;
    private final OrganizationDAO organizationDAO;

    @Autowired
    public AnalyticsService(AnalyticsDAO analyticsDAO, ProblemDAO problemDAO,
                            UserDAO userDAO, ProjectDAO projectDAO,
                            SolutionDAO solutionDAO, OrganizationDAO organizationDAO) {
        this.analyticsDAO = analyticsDAO;
        this.problemDAO = problemDAO;
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.solutionDAO = solutionDAO;
        this.organizationDAO = organizationDAO;
    }

    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProblems", problemDAO.countAll());
        stats.put("totalUsers", userDAO.countAll());
        stats.put("totalProjects", projectDAO.countAll());
        stats.put("totalSolutions", solutionDAO.countAll());
        stats.put("totalOrganizations", organizationDAO.countAll());
        stats.put("pendingProblems", problemDAO.countByStatus("PENDING"));
        stats.put("pendingSolutions", solutionDAO.countPending());
        return stats;
    }

    public Map<String, Integer> getProblemsByDomain() {
        return analyticsDAO.getProblemsByDomain();
    }

    public Map<String, Integer> getProblemsByDifficulty() {
        return analyticsDAO.getProblemsByDifficulty();
    }

    public Map<String, Integer> getProblemsByProjectType() {
        return analyticsDAO.getProblemsByProjectType();
    }

    public Map<String, Integer> getPopularTechnologies() {
        return analyticsDAO.getPopularTechnologies();
    }

    public List<Map<String, Object>> getMostViewedProblems(int limit) {
        return analyticsDAO.getMostViewedProblems(limit);
    }

    public List<Map<String, Object>> getMostSolvedProblems(int limit) {
        return analyticsDAO.getMostSolvedProblems(limit);
    }
}
