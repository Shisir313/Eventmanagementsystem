package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection helper using JDBC.
 * Update the URL, USERNAME and PASSWORD constants to match your local MySQL setup.
 */
public class DBConnection {
    // TODO: Change these to match your MySQL configuration
    private static final String URL = "jdbc:mysql://localhost:3306/college_events";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1shisir123";

    static {
        try {
            // Load MySQL JDBC driver (optional for modern drivers but safe)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add the MySQL connector jar to classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Get a database connection. Caller is responsible for closing it.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
