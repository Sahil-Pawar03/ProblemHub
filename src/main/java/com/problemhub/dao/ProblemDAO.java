package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import com.problemhub.model.Problem;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Problem operations using direct JDBC and PreparedStatement.
 */
@Repository
public class ProblemDAO {

    public Optional<Problem> findById(int id) {
        String sql = "SELECT p.*, " +
                     "(SELECT COUNT(*) FROM solutions s WHERE s.problem_id = p.id AND s.status = 'APPROVED') AS solutions_count " +
                     "FROM problems p WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProblem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Problem> findAllApproved() {
        return searchAndFilter(null, null, null, null, null, null, "APPROVED");
    }

    public List<Problem> searchAndFilter(String query, String domain, String technology,
                                         String difficulty, String duration, String projectType,
                                         String status) {
        List<Problem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, " +
                "(SELECT COUNT(*) FROM solutions s WHERE s.problem_id = p.id AND s.status = 'APPROVED') AS solutions_count " +
                "FROM problems p WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND p.status = ? ");
            params.add(status.trim());
        }

        if (domain != null && !domain.trim().isEmpty() && !"ALL".equalsIgnoreCase(domain)) {
            sql.append("AND LOWER(p.domain) = LOWER(?) ");
            params.add(domain.trim());
        }

        if (difficulty != null && !difficulty.trim().isEmpty() && !"ALL".equalsIgnoreCase(difficulty)) {
            sql.append("AND LOWER(p.difficulty) = LOWER(?) ");
            params.add(difficulty.trim());
        }

        if (duration != null && !duration.trim().isEmpty() && !"ALL".equalsIgnoreCase(duration)) {
            sql.append("AND LOWER(p.duration) = LOWER(?) ");
            params.add(duration.trim());
        }

        if (projectType != null && !projectType.trim().isEmpty() && !"ALL".equalsIgnoreCase(projectType)) {
            sql.append("AND LOWER(p.project_type) = LOWER(?) ");
            params.add(projectType.trim());
        }

        if (technology != null && !technology.trim().isEmpty() && !"ALL".equalsIgnoreCase(technology)) {
            sql.append("AND (LOWER(p.suggested_tech) LIKE ? OR LOWER(p.required_skills) LIKE ?) ");
            String techPattern = "%" + technology.trim().toLowerCase() + "%";
            params.add(techPattern);
            params.add(techPattern);
        }

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(p.title) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.domain) LIKE ? OR LOWER(p.organization_name) LIKE ?) ");
            String qPattern = "%" + query.trim().toLowerCase() + "%";
            params.add(qPattern);
            params.add(qPattern);
            params.add(qPattern);
            params.add(qPattern);
        }

        sql.append("ORDER BY p.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProblem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.searchAndFilter: " + e.getMessage());
        }
        return list;
    }

    public int createProblem(Problem p) {
        String sql = "INSERT INTO problems (title, description, real_world_context, current_challenges, " +
                     "expected_outcome, why_it_matters, deliverables, possible_extensions, domain, difficulty, " +
                     "duration, project_type, source_type, organization_name, contact_info, required_skills, " +
                     "suggested_tech, status, is_verified, views_count, created_by_user_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getTitle());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getRealWorldContext());
            ps.setString(4, p.getCurrentChallenges());
            ps.setString(5, p.getExpectedOutcome());
            ps.setString(6, p.getWhyItMatters());
            ps.setString(7, p.getDeliverables());
            ps.setString(8, p.getPossibleExtensions());
            ps.setString(9, p.getDomain() != null ? p.getDomain() : "General");
            ps.setString(10, p.getDifficulty() != null ? p.getDifficulty() : "Intermediate");
            ps.setString(11, p.getDuration() != null ? p.getDuration() : "2–3 Months");
            ps.setString(12, p.getProjectType() != null ? p.getProjectType() : "Major Project");
            ps.setString(13, p.getSourceType() != null ? p.getSourceType() : "Industry");
            ps.setString(14, p.getOrganizationName() != null ? p.getOrganizationName() : "Partner Organization");
            ps.setString(15, p.getContactInfo() != null ? p.getContactInfo() : "contact@problemhub.com");
            ps.setString(16, p.getRequiredSkills());
            ps.setString(17, p.getSuggestedTech());
            ps.setString(18, p.getStatus() != null ? p.getStatus() : "PENDING");
            ps.setBoolean(19, p.isVerified());
            ps.setInt(20, p.getViewsCount());
            if (p.getCreatedByUserId() != null) {
                ps.setInt(21, p.getCreatedByUserId());
            } else {
                ps.setNull(21, Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        p.setId(gk.getInt(1));
                        return p.getId();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.createProblem: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateStatus(int id, String status, boolean isVerified) {
        String sql = "UPDATE problems SET status = ?, is_verified = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setBoolean(2, isVerified);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.updateStatus: " + e.getMessage());
        }
        return false;
    }

    public void incrementViews(int id) {
        String sql = "UPDATE problems SET views_count = views_count + 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.incrementViews: " + e.getMessage());
        }
    }

    public List<Problem> findRecent(int limit) {
        List<Problem> list = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "(SELECT COUNT(*) FROM solutions s WHERE s.problem_id = p.id AND s.status = 'APPROVED') AS solutions_count " +
                     "FROM problems p WHERE p.status = 'APPROVED' ORDER BY p.id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProblem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.findRecent: " + e.getMessage());
        }
        return list;
    }

    public List<Problem> findByCreatedUserId(int userId) {
        List<Problem> list = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "(SELECT COUNT(*) FROM solutions s WHERE s.problem_id = p.id AND s.status = 'APPROVED') AS solutions_count " +
                     "FROM problems p WHERE p.created_by_user_id = ? ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProblem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.findByCreatedUserId: " + e.getMessage());
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM problems";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.countAll: " + e.getMessage());
        }
        return 0;
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM problems WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in ProblemDAO.countByStatus: " + e.getMessage());
        }
        return 0;
    }

    private Problem mapResultSetToProblem(ResultSet rs) throws SQLException {
        Problem p = new Problem();
        p.setId(rs.getInt("id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setRealWorldContext(rs.getString("real_world_context"));
        p.setCurrentChallenges(rs.getString("current_challenges"));
        p.setExpectedOutcome(rs.getString("expected_outcome"));
        p.setWhyItMatters(rs.getString("why_it_matters"));
        p.setDeliverables(rs.getString("deliverables"));
        p.setPossibleExtensions(rs.getString("possible_extensions"));
        p.setDomain(rs.getString("domain"));
        p.setDifficulty(rs.getString("difficulty"));
        p.setDuration(rs.getString("duration"));
        p.setProjectType(rs.getString("project_type"));
        p.setSourceType(rs.getString("source_type"));
        p.setOrganizationName(rs.getString("organization_name"));
        p.setContactInfo(rs.getString("contact_info"));
        p.setRequiredSkills(rs.getString("required_skills"));
        p.setSuggestedTech(rs.getString("suggested_tech"));
        p.setStatus(rs.getString("status"));
        p.setVerified(rs.getBoolean("is_verified"));
        p.setViewsCount(rs.getInt("views_count"));
        int createdBy = rs.getInt("created_by_user_id");
        if (!rs.wasNull()) {
            p.setCreatedByUserId(createdBy);
        }
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            p.setSolutionsCount(rs.getInt("solutions_count"));
        } catch (SQLException ignored) {
            p.setSolutionsCount(0);
        }

        return p;
    }
}
