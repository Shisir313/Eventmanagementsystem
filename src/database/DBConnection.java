package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern — only ONE connection instance exists.
 * Encapsulation : constructor is private, instance is controlled.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/college_events";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1shisir123";

    private static DBConnection instance = null;
    private Connection connection        = null;

    // Private constructor — prevents new DBConnection() from outside
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Global access point — creates instance only once
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Connection retrieval failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Close the underlying connection when the application is shutting down.
     */
    public void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}