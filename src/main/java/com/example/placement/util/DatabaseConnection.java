package com.example.placement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SINGLETON PATTERN demonstrating ENCAPSULATION of database connection logic.
 */
public class DatabaseConnection {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "placement_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "subhradeep3";

    private DatabaseConnection() {
        // Private constructor to prevent direct instantiation
    }

    public static Connection getConnection() throws SQLException {
        String host = getEnvOrProp("DB_HOST", DEFAULT_HOST);
        String port = getEnvOrProp("DB_PORT", DEFAULT_PORT);
        String dbName = getEnvOrProp("DB_NAME", DEFAULT_DB);
        String user = getEnvOrProp("DB_USER", DEFAULT_USER);
        String password = getEnvOrProp("DB_PASSWORD", DEFAULT_PASSWORD);

        String url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            host, port, dbName
        );

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[JDBC ERROR] MySQL Driver class not found: " + e.getMessage());
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnvOrProp(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) return value;
        value = System.getProperty(key);
        if (value != null && !value.isEmpty()) return value;
        return defaultValue;
    }
}
