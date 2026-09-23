package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import com.problemhub.model.Project;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Project operations using direct JDBC and PreparedStatement.
 */
@Repository
public class ProjectDAO {

    public Optional<Project> findById(int id) {
        String sql = "SELECT p.*, prob.title AS problem_title, prob.domain AS problem_domain, u.full_name AS student_name " +
                     "FROM projects p " +
                     "JOIN problems prob ON p.problem_id = prob.id " +
                     "JOIN users u ON p.student_user_id = u.id " +
                     "WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProject(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Project> findByStudentId(int studentUserId, String statusFilter) {
        List<Project> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, prob.title AS problem_title, prob.domain AS problem_domain, u.full_name AS student_name " +
                "FROM projects p " +
                "JOIN problems prob ON p.problem_id = prob.id " +
                "JOIN users u ON p.student_user_id = u.id " +
                "WHERE p.student_user_id = ? "
        );

        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND p.status = ? ");
        }
        sql.append("ORDER BY p.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, studentUserId);
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
                ps.setString(2, statusFilter.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProject(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.findByStudentId: " + e.getMessage());
        }
        return list;
    }

    public List<Project> findByProblemId(int problemId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, prob.title AS problem_title, prob.domain AS problem_domain, u.full_name AS student_name " +
                     "FROM projects p " +
                     "JOIN problems prob ON p.problem_id = prob.id " +
                     "JOIN users u ON p.student_user_id = u.id " +
                     "WHERE p.problem_id = ? ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, problemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProject(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.findByProblemId: " + e.getMessage());
        }
        return list;
    }

    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, prob.title AS problem_title, prob.domain AS problem_domain, u.full_name AS student_name " +
                     "FROM projects p " +
                     "JOIN problems prob ON p.problem_id = prob.id " +
                     "JOIN users u ON p.student_user_id = u.id " +
                     "ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.findAll: " + e.getMessage());
        }
        return list;
    }

    public int createProject(Project p) {
        String sql = "INSERT INTO projects (problem_id, student_user_id, title, description, team_members, " +
                     "technologies_used, status, progress_percent, start_date, repo_url, documentation_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getProblemId());
            ps.setInt(2, p.getStudentUserId());
            ps.setString(3, p.getTitle());
            ps.setString(4, p.getDescription());
            ps.setString(5, p.getTeamMembers());
            ps.setString(6, p.getTechnologiesUsed());
            ps.setString(7, p.getStatus() != null ? p.getStatus() : "IN_PROGRESS");
            ps.setInt(8, p.getProgressPercent());
            ps.setDate(9, p.getStartDate() != null ? p.getStartDate() : new Date(System.currentTimeMillis()));
            ps.setString(10, p.getRepoUrl());
            ps.setString(11, p.getDocumentationUrl());

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
            System.err.println("Error in ProjectDAO.createProject: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateProgressAndStatus(int id, int progress, String status, String repoUrl, String docUrl) {
        String sql = "UPDATE projects SET progress_percent = ?, status = ?, repo_url = ?, documentation_url = ?, " +
                     "completion_date = CASE WHEN ? = 'COMPLETED' THEN CURRENT_DATE ELSE completion_date END " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, progress);
            ps.setString(2, status);
            ps.setString(3, repoUrl);
            ps.setString(4, docUrl);
            ps.setString(5, status);
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.updateProgressAndStatus: " + e.getMessage());
        }
        return false;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM projects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.countAll: " + e.getMessage());
        }
        return 0;
    }

    public int countByStudentId(int studentUserId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE student_user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.countByStudentId: " + e.getMessage());
        }
        return 0;
    }

    public int countCompletedByStudentId(int studentUserId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE student_user_id = ? AND status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in ProjectDAO.countCompletedByStudentId: " + e.getMessage());
        }
        return 0;
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setId(rs.getInt("id"));
        p.setProblemId(rs.getInt("problem_id"));
        p.setStudentUserId(rs.getInt("student_user_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setTeamMembers(rs.getString("team_members"));
        p.setTechnologiesUsed(rs.getString("technologies_used"));
        p.setStatus(rs.getString("status"));
        p.setProgressPercent(rs.getInt("progress_percent"));
        p.setStartDate(rs.getDate("start_date"));
        p.setCompletionDate(rs.getDate("completion_date"));
        p.setRepoUrl(rs.getString("repo_url"));
        p.setDocumentationUrl(rs.getString("documentation_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            p.setProblemTitle(rs.getString("problem_title"));
            p.setProblemDomain(rs.getString("problem_domain"));
            p.setStudentName(rs.getString("student_name"));
        } catch (SQLException ignored) {}

        return p;
    }
}
