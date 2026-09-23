package com.problemhub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Reusable DatabaseConnection utility class for direct JDBC operations.
 * Uses Spring's configured DataSource connection pool (HikariCP),
 * with fallback to DriverManager if needed.
 */
@Component
public class DatabaseConnection {

    private static DataSource dataSource;

    // Default connection properties (fallback)
    private static final String URL = "jdbc:mysql://localhost:3306/problemhub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "dkte";

    @Autowired
    public DatabaseConnection(DataSource ds) {
        DatabaseConnection.dataSource = ds;
    }

    /**
     * Obtains a live JDBC Connection from the pool or DriverManager.
     * Always remember to use try-with-resources to close the connection properly.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
