package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton Pattern — only ONE connection instance exists.
 * Encapsulation : constructor is private, instance is controlled.
 *
 * Supports two modes:
 *  - MySQL (default) — uses JDBC URL from env/system or defaults
 *  - H2 (in-memory)  — set DB_MODE=H2 to run an in-memory DB for quick testing
 */
public class DBConnection {

    private static final String DB_MODE = System.getenv().getOrDefault("DB_MODE",
            System.getProperty("db.mode", "MYSQL"));

    private static final boolean USE_H2 = "H2".equalsIgnoreCase(DB_MODE);

    private static final String URL_MYSQL = System.getenv().getOrDefault("DB_URL",
            System.getProperty("db.url", "jdbc:mysql://localhost:3306/college_events"));
    private static final String USERNAME_MYSQL = System.getenv().getOrDefault("DB_USER",
            System.getProperty("db.user", "root"));
    private static final String PASSWORD_MYSQL = System.getenv().getOrDefault("DB_PASS",
            System.getProperty("db.pass", "1shisir123"));

    // H2 in-memory defaults
    private static final String URL_H2      = System.getenv().getOrDefault("DB_URL",
            System.getProperty("db.url", "jdbc:h2:mem:college_events;DB_CLOSE_DELAY=-1"));
    private static final String USERNAME_H2 = System.getenv().getOrDefault("DB_USER",
            System.getProperty("db.user", "sa"));
    private static final String PASSWORD_H2 = System.getenv().getOrDefault("DB_PASS",
            System.getProperty("db.pass", ""));

    private static DBConnection instance = null;
    private Connection connection        = null;

    // Private constructor — prevents new DBConnection() from outside
    private DBConnection() {
        try {
            if (USE_H2) {
                Class.forName("org.h2.Driver");
                this.connection = DriverManager.getConnection(URL_H2, USERNAME_H2, PASSWORD_H2);
                System.out.println("H2 in-memory database started.");
                ensureH2Schema();
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.connection = DriverManager.getConnection(URL_MYSQL, USERNAME_MYSQL, PASSWORD_MYSQL);
                System.out.println("Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ensure minimal schema exists when running H2 for tests
    private void ensureH2Schema() {
        String[] stmts = new String[]{
            "CREATE TABLE IF NOT EXISTS student (student_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE, password VARCHAR(255), department VARCHAR(100));",
            "CREATE TABLE IF NOT EXISTS organizer (organizer_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE, password VARCHAR(255), contact VARCHAR(100));",
            "CREATE TABLE IF NOT EXISTS admin (admin_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE, password VARCHAR(255));",
            "CREATE TABLE IF NOT EXISTS event (event_id INT AUTO_INCREMENT PRIMARY KEY, event_name VARCHAR(255), event_date DATE, location VARCHAR(255), description CLOB, organizer_id INT, status VARCHAR(50));",
            "CREATE TABLE IF NOT EXISTS registration (registration_id INT AUTO_INCREMENT PRIMARY KEY, student_id INT, event_id INT, registration_date DATE, status VARCHAR(50));",
            // approval no longer stores admin_id — remove admin_id column
            "CREATE TABLE IF NOT EXISTS approval (approval_id INT AUTO_INCREMENT PRIMARY KEY, registration_id INT, status VARCHAR(50), remarks CLOB);"
        };
        try (Statement st = connection.createStatement()) {
            for (String s : stmts) st.execute(s);
            System.out.println("H2 schema ensured.");
        } catch (SQLException e) {
            System.err.println("Failed to create H2 schema: " + e.getMessage());
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
                if (USE_H2) {
                    connection = DriverManager.getConnection(URL_H2, USERNAME_H2, PASSWORD_H2);
                } else {
                    connection = DriverManager.getConnection(URL_MYSQL, USERNAME_MYSQL, PASSWORD_MYSQL);
                }
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