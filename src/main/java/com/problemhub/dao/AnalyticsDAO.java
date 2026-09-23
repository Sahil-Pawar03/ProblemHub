package com.problemhub.dao;

import com.problemhub.config.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Data Access Object for platform analytics, metrics, and chart data.
 */
@Repository
public class AnalyticsDAO {

    public Map<String, Integer> getProblemsByDomain() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT domain, COUNT(*) AS cnt FROM problems GROUP BY domain ORDER BY cnt DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("domain"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            System.err.println("Error in AnalyticsDAO.getProblemsByDomain: " + e.getMessage());
        }
        return map;
    }

    public Map<String, Integer> getProblemsByDifficulty() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT difficulty, COUNT(*) AS cnt FROM problems GROUP BY difficulty ORDER BY FIELD(difficulty, 'Beginner', 'Intermediate', 'Advanced')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("difficulty"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            System.err.println("Error in AnalyticsDAO.getProblemsByDifficulty: " + e.getMessage());
        }
        return map;
    }

    public Map<String, Integer> getProblemsByProjectType() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT project_type, COUNT(*) AS cnt FROM problems GROUP BY project_type ORDER BY cnt DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("project_type"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            System.err.println("Error in AnalyticsDAO.getProblemsByProjectType: " + e.getMessage());
        }
        return map;
    }

    public List<Map<String, Object>> getMostViewedProblems(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, title, domain, views_count FROM problems ORDER BY views_count DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("title", rs.getString("title"));
                    item.put("domain", rs.getString("domain"));
                    item.put("views", rs.getInt("views_count"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in AnalyticsDAO.getMostViewedProblems: " + e.getMessage());
        }
        return list;
    }

    public List<Map<String, Object>> getMostSolvedProblems(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.id, p.title, p.domain, COUNT(s.id) AS solutions_count " +
                     "FROM problems p " +
                     "JOIN solutions s ON s.problem_id = p.id " +
                     "WHERE s.status = 'APPROVED' " +
                     "GROUP BY p.id, p.title, p.domain " +
                     "ORDER BY solutions_count DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("title", rs.getString("title"));
                    item.put("domain", rs.getString("domain"));
                    item.put("solvedCount", rs.getInt("solutions_count"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in AnalyticsDAO.getMostSolvedProblems: " + e.getMessage());
        }
        return list;
    }

    public Map<String, Integer> getPopularTechnologies() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT suggested_tech FROM problems WHERE suggested_tech IS NOT NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String raw = rs.getString("suggested_tech");
                if (raw != null) {
                    for (String t : raw.split(",")) {
                        String clean = t.trim();
                        if (!clean.isEmpty()) {
                            counts.put(clean, counts.getOrDefault(clean, 0) + 1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in AnalyticsDAO.getPopularTechnologies: " + e.getMessage());
        }

        // Sort descending
        List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Integer> sorted = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : list) {
            sorted.put(entry.getKey(), entry.getValue());
            if (++count >= 8) break;
        }
        return sorted;
    }
}
