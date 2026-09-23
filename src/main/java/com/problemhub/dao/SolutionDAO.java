package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import com.problemhub.model.Solution;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Solution operations using direct JDBC and PreparedStatement.
 */
@Repository
public class SolutionDAO {

    public Optional<Solution> findById(int id) {
        String sql = "SELECT s.*, prob.title AS problem_title, prob.domain AS problem_domain, " +
                     "COALESCE(AVG(r.rating_value), 0.0) AS avg_rating, " +
                     "COUNT(r.id) AS ratings_count " +
                     "FROM solutions s " +
                     "JOIN problems prob ON s.problem_id = prob.id " +
                     "LEFT JOIN ratings r ON r.solution_id = s.id " +
                     "WHERE s.id = ? " +
                     "GROUP BY s.id, prob.title, prob.domain";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSolution(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in SolutionDAO.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Solution> findByProblemId(int problemId) {
        List<Solution> list = new ArrayList<>();
        String sql = "SELECT s.*, prob.title AS problem_title, prob.domain AS problem_domain, " +
                     "COALESCE(AVG(r.rating_value), 0.0) AS avg_rating, " +
                     "COUNT(r.id) AS ratings_count " +
                     "FROM solutions s " +
                     "JOIN problems prob ON s.problem_id = prob.id " +
                     "LEFT JOIN ratings r ON r.solution_id = s.id " +
                     "WHERE s.problem_id = ? AND s.status = 'APPROVED' " +
                     "GROUP BY s.id, prob.title, prob.domain ORDER BY s.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, problemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSolution(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in SolutionDAO.findByProblemId: " + e.getMessage());
        }
        return list;
    }

    public List<Solution> searchAndFilter(String query, String domain, String tech,
                                          Integer year, String difficulty, String projectType,
                                          String status) {
        List<Solution> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.*, prob.title AS problem_title, prob.domain AS problem_domain, " +
                "COALESCE(AVG(r.rating_value), 0.0) AS avg_rating, " +
                "COUNT(r.id) AS ratings_count " +
                "FROM solutions s " +
                "JOIN problems prob ON s.problem_id = prob.id " +
                "LEFT JOIN ratings r ON r.solution_id = s.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND s.status = ? ");
            params.add(status.trim());
        }

        if (domain != null && !domain.trim().isEmpty() && !"ALL".equalsIgnoreCase(domain)) {
            sql.append("AND LOWER(prob.domain) = LOWER(?) ");
            params.add(domain.trim());
        }

        if (difficulty != null && !difficulty.trim().isEmpty() && !"ALL".equalsIgnoreCase(difficulty)) {
            sql.append("AND LOWER(prob.difficulty) = LOWER(?) ");
            params.add(difficulty.trim());
        }

        if (projectType != null && !projectType.trim().isEmpty() && !"ALL".equalsIgnoreCase(projectType)) {
            sql.append("AND LOWER(prob.project_type) = LOWER(?) ");
            params.add(projectType.trim());
        }

        if (year != null && year > 0) {
            sql.append("AND s.completion_year = ? ");
            params.add(year);
        }

        if (tech != null && !tech.trim().isEmpty() && !"ALL".equalsIgnoreCase(tech)) {
            sql.append("AND LOWER(s.technologies) LIKE ? ");
            params.add("%" + tech.trim().toLowerCase() + "%");
        }

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(s.title) LIKE ? OR LOWER(s.solution_overview) LIKE ? OR LOWER(prob.title) LIKE ? OR LOWER(s.college) LIKE ?) ");
            String qPattern = "%" + query.trim().toLowerCase() + "%";
            params.add(qPattern);
            params.add(qPattern);
            params.add(qPattern);
            params.add(qPattern);
        }

        sql.append("GROUP BY s.id, prob.title, prob.domain ORDER BY s.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSolution(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in SolutionDAO.searchAndFilter: " + e.getMessage());
        }
        return list;
    }

    public int createSolution(Solution s) {
        String sql = "INSERT INTO solutions (project_id, problem_id, submitted_by_user_id, title, team_names, " +
                     "college, completion_year, technologies, solution_overview, architecture_details, " +
                     "implementation_details, key_features, results, challenges, limitations, future_scope, " +
                     "github_url, documentation_url, demo_url, screenshots_json, status, is_verified) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (s.getProjectId() != null) {
                ps.setInt(1, s.getProjectId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, s.getProblemId());
            ps.setInt(3, s.getSubmittedByUserId());
            ps.setString(4, s.getTitle());
            ps.setString(5, s.getTeamNames());
            ps.setString(6, s.getCollege());
            ps.setInt(7, s.getCompletionYear());
            ps.setString(8, s.getTechnologies());
            ps.setString(9, s.getSolutionOverview());
            ps.setString(10, s.getArchitectureDetails());
            ps.setString(11, s.getImplementationDetails());
            ps.setString(12, s.getKeyFeatures());
            ps.setString(13, s.getResults());
            ps.setString(14, s.getChallenges());
            ps.setString(15, s.getLimitations());
            ps.setString(16, s.getFutureScope());
            ps.setString(17, s.getGithubUrl());
            ps.setString(18, s.getDocumentationUrl());
            ps.setString(19, s.getDemoUrl());
            ps.setString(20, s.getScreenshotsJson());
            ps.setString(21, s.getStatus() != null ? s.getStatus() : "PENDING");
            ps.setBoolean(22, s.isVerified());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        s.setId(gk.getInt(1));
                        return s.getId();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in SolutionDAO.createSolution: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateStatus(int id, String status, boolean isVerified) {
        String sql = "UPDATE solutions SET status = ?, is_verified = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setBoolean(2, isVerified);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in SolutionDAO.updateStatus: " + e.getMessage());
        }
        return false;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM solutions";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error in SolutionDAO.countAll: " + e.getMessage());
        }
        return 0;
    }

    public int countPending() {
        String sql = "SELECT COUNT(*) FROM solutions WHERE status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error in SolutionDAO.countPending: " + e.getMessage());
        }
        return 0;
    }

    private Solution mapResultSetToSolution(ResultSet rs) throws SQLException {
        Solution s = new Solution();
        s.setId(rs.getInt("id"));
        int pId = rs.getInt("project_id");
        if (!rs.wasNull()) s.setProjectId(pId);
        s.setProblemId(rs.getInt("problem_id"));
        s.setSubmittedByUserId(rs.getInt("submitted_by_user_id"));
        s.setTitle(rs.getString("title"));
        s.setTeamNames(rs.getString("team_names"));
        s.setCollege(rs.getString("college"));
        s.setCompletionYear(rs.getInt("completion_year"));
        s.setTechnologies(rs.getString("technologies"));
        s.setSolutionOverview(rs.getString("solution_overview"));
        s.setArchitectureDetails(rs.getString("architecture_details"));
        s.setImplementationDetails(rs.getString("implementation_details"));
        s.setKeyFeatures(rs.getString("key_features"));
        s.setResults(rs.getString("results"));
        s.setChallenges(rs.getString("challenges"));
        s.setLimitations(rs.getString("limitations"));
        s.setFutureScope(rs.getString("future_scope"));
        s.setGithubUrl(rs.getString("github_url"));
        s.setDocumentationUrl(rs.getString("documentation_url"));
        s.setDemoUrl(rs.getString("demo_url"));
        s.setScreenshotsJson(rs.getString("screenshots_json"));
        s.setStatus(rs.getString("status"));
        s.setVerified(rs.getBoolean("is_verified"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            s.setProblemTitle(rs.getString("problem_title"));
            s.setProblemDomain(rs.getString("problem_domain"));
            s.setAverageRating(Math.round(rs.getDouble("avg_rating") * 10.0) / 10.0);
            s.setRatingsCount(rs.getInt("ratings_count"));
        } catch (SQLException ignored) {}

        return s;
    }
}
