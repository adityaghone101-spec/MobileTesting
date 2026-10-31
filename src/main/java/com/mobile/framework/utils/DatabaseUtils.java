package com.mobile.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DatabaseUtils provides a common interface for database operations.
 * Supports connecting to MySQL, SQLite, etc., based on configuration.
 */
public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private Connection connection;

    /**
     * Initializes the database connection using properties from config.properties.
     */
    public DatabaseUtils() {
        String url = ConfigReader.getProperty("db.url");
        String user = ConfigReader.getProperty("db.username");
        String password = ConfigReader.getProperty("db.password");
        String driver = ConfigReader.getProperty("db.driver");

        try {
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, password);
            logger.info("Connected to database: {}", url);
        } catch (Exception e) {
            logger.error("Failed to connect to database: {}", e.getMessage());
            throw new RuntimeException("Database connection failure", e);
        }
    }

    /**
     * Executes a query and returns the first row as a Map (Column Name -> Value).
     * Useful for retrieving credentials or expected data for matching.
     */
    public Map<String, String> getDataRow(String query) {
        Map<String, String> rowData = new HashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(metaData.getColumnName(i), rs.getString(i));
                }
            }
        } catch (SQLException e) {
            logger.error("Error executing query: {}", e.getMessage());
        }
        return rowData;
    }

    /**
     * Verifies if specific data exists in a table.
     */
    public boolean isDataPresent(String tableName, String columnName, String expectedValue) {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s'", tableName, columnName, expectedValue);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error verifying data existence: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed.");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection: {}", e.getMessage());
        }
    }
}
