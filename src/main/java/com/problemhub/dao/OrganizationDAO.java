package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import com.problemhub.model.Organization;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Organizations using direct JDBC.
 */
@Repository
public class OrganizationDAO {

    public Optional<Organization> findById(int id) {
        String sql = "SELECT * FROM organizations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToOrganization(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in OrganizationDAO.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Organization> findByUserId(int userId) {
        String sql = "SELECT * FROM organizations WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToOrganization(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in OrganizationDAO.findByUserId: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Organization> findAll() {
        List<Organization> list = new ArrayList<>();
        String sql = "SELECT * FROM organizations ORDER BY org_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToOrganization(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in OrganizationDAO.findAll: " + e.getMessage());
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM organizations";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error in OrganizationDAO.countAll: " + e.getMessage());
        }
        return 0;
    }

    public int createOrganization(Organization org) {
        String sql = "INSERT INTO organizations (user_id, org_name, org_type, website, description, contact_email, contact_phone, is_verified) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (org.getUserId() != null) {
                ps.setInt(1, org.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, org.getOrgName());
            ps.setString(3, org.getOrgType());
            ps.setString(4, org.getWebsite());
            ps.setString(5, org.getDescription());
            ps.setString(6, org.getContactEmail());
            ps.setString(7, org.getContactPhone());
            ps.setBoolean(8, org.isVerified());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        org.setId(gk.getInt(1));
                        return org.getId();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in OrganizationDAO.createOrganization: " + e.getMessage());
        }
        return -1;
    }

    private Organization mapResultSetToOrganization(ResultSet rs) throws SQLException {
        Organization org = new Organization();
        org.setId(rs.getInt("id"));
        int uId = rs.getInt("user_id");
        if (!rs.wasNull()) org.setUserId(uId);
        org.setOrgName(rs.getString("org_name"));
        org.setOrgType(rs.getString("org_type"));
        org.setWebsite(rs.getString("website"));
        org.setDescription(rs.getString("description"));
        org.setContactEmail(rs.getString("contact_email"));
        org.setContactPhone(rs.getString("contact_phone"));
        org.setVerified(rs.getBoolean("is_verified"));
        org.setCreatedAt(rs.getTimestamp("created_at"));
        return org;
    }
}
