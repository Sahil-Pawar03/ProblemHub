package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import com.problemhub.model.Bookmark;
import com.problemhub.model.Problem;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bookmarks using direct JDBC.
 */
@Repository
public class BookmarkDAO {

    public boolean addBookmark(int userId, int problemId) {
        String sql = "INSERT IGNORE INTO bookmarks (user_id, problem_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, problemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in BookmarkDAO.addBookmark: " + e.getMessage());
        }
        return false;
    }

    public boolean removeBookmark(int userId, int problemId) {
        String sql = "DELETE FROM bookmarks WHERE user_id = ? AND problem_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, problemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in BookmarkDAO.removeBookmark: " + e.getMessage());
        }
        return false;
    }

    public boolean isBookmarked(int userId, int problemId) {
        String sql = "SELECT 1 FROM bookmarks WHERE user_id = ? AND problem_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, problemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error in BookmarkDAO.isBookmarked: " + e.getMessage());
        }
        return false;
    }

    public List<Bookmark> getBookmarksByUserId(int userId) {
        List<Bookmark> list = new ArrayList<>();
        String sql = "SELECT b.id AS bookmark_id, b.user_id, b.problem_id, b.created_at AS bookmark_date, " +
                     "p.*, " +
                     "(SELECT COUNT(*) FROM solutions s WHERE s.problem_id = p.id AND s.status = 'APPROVED') AS solutions_count " +
                     "FROM bookmarks b " +
                     "JOIN problems p ON b.problem_id = p.id " +
                     "WHERE b.user_id = ? ORDER BY b.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bookmark b = new Bookmark();
                    b.setId(rs.getInt("bookmark_id"));
                    b.setUserId(rs.getInt("user_id"));
                    b.setProblemId(rs.getInt("problem_id"));
                    b.setCreatedAt(rs.getTimestamp("bookmark_date"));

                    Problem p = new Problem();
                    p.setId(rs.getInt("id"));
                    p.setTitle(rs.getString("title"));
                    p.setDescription(rs.getString("description"));
                    p.setDomain(rs.getString("domain"));
                    p.setDifficulty(rs.getString("difficulty"));
                    p.setDuration(rs.getString("duration"));
                    p.setProjectType(rs.getString("project_type"));
                    p.setSourceType(rs.getString("source_type"));
                    p.setOrganizationName(rs.getString("organization_name"));
                    p.setSuggestedTech(rs.getString("suggested_tech"));
                    p.setStatus(rs.getString("status"));
                    p.setVerified(rs.getBoolean("is_verified"));
                    p.setViewsCount(rs.getInt("views_count"));
                    p.setSolutionsCount(rs.getInt("solutions_count"));
                    p.setBookmarkedByCurrentUser(true);

                    b.setProblem(p);
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in BookmarkDAO.getBookmarksByUserId: " + e.getMessage());
        }
        return list;
    }

    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM bookmarks WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in BookmarkDAO.countByUserId: " + e.getMessage());
        }
        return 0;
    }
}
