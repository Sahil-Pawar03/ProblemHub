package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import com.problemhub.model.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User operations using direct JDBC and PreparedStatement.
 */
@Repository
public class UserDAO {

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.findByEmail: " + e.getMessage());
        }
        return Optional.empty();
    }

    public int createUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, college, department, year_of_study, skills, interests, bio, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole() != null ? user.getRole().toUpperCase() : "STUDENT");
            ps.setString(5, user.getCollege());
            ps.setString(6, user.getDepartment());
            ps.setString(7, user.getYearOfStudy());
            ps.setString(8, user.getSkills());
            ps.setString(9, user.getInterests());
            ps.setString(10, user.getBio());
            ps.setBoolean(11, user.isActive());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return user.getId();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.createUser: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, college = ?, department = ?, year_of_study = ?, " +
                     "skills = ?, interests = ?, bio = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getCollege());
            ps.setString(3, user.getDepartment());
            ps.setString(4, user.getYearOfStudy());
            ps.setString(5, user.getSkills());
            ps.setString(6, user.getInterests());
            ps.setString(7, user.getBio());
            ps.setInt(8, user.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.updateUser: " + e.getMessage());
        }
        return false;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.findAll: " + e.getMessage());
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.countAll: " + e.getMessage());
        }
        return 0;
    }

    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.countByRole: " + e.getMessage());
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setCollege(rs.getString("college"));
        u.setDepartment(rs.getString("department"));
        u.setYearOfStudy(rs.getString("year_of_study"));
        u.setSkills(rs.getString("skills"));
        u.setInterests(rs.getString("interests"));
        u.setBio(rs.getString("bio"));
        u.setActive(rs.getBoolean("is_active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
