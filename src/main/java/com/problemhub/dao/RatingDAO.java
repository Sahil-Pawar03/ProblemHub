package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import com.problemhub.model.Rating;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Solution Ratings using direct JDBC.
 */
@Repository
public class RatingDAO {

    public boolean addOrUpdateRating(int solutionId, int userId, int ratingValue, String reviewComment) {
        String sql = "INSERT INTO ratings (solution_id, user_id, rating_value, review_comment) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE rating_value = VALUES(rating_value), review_comment = VALUES(review_comment), created_at = CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, solutionId);
            ps.setInt(2, userId);
            ps.setInt(3, ratingValue);
            ps.setString(4, reviewComment);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in RatingDAO.addOrUpdateRating: " + e.getMessage());
        }
        return false;
    }

    public List<Rating> findBySolutionId(int solutionId) {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS user_name, u.role AS user_role " +
                     "FROM ratings r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "WHERE r.solution_id = ? " +
                     "ORDER BY r.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, solutionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating r = new Rating();
                    r.setId(rs.getInt("id"));
                    r.setSolutionId(rs.getInt("solution_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setRatingValue(rs.getInt("rating_value"));
                    r.setReviewComment(rs.getString("review_comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setUserName(rs.getString("user_name"));
                    r.setUserRole(rs.getString("user_role"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in RatingDAO.findBySolutionId: " + e.getMessage());
        }
        return list;
    }

    public Optional<Rating> getUserRating(int solutionId, int userId) {
        String sql = "SELECT * FROM ratings WHERE solution_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, solutionId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rating r = new Rating();
                    r.setId(rs.getInt("id"));
                    r.setSolutionId(rs.getInt("solution_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setRatingValue(rs.getInt("rating_value"));
                    r.setReviewComment(rs.getString("review_comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    return Optional.of(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in RatingDAO.getUserRating: " + e.getMessage());
        }
        return Optional.empty();
    }
}
