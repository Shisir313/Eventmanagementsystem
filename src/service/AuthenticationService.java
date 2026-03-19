package service;

import database.DBConnection;
import model.*;
import java.sql.*;

/**
 * Singleton + Factory Pattern.
 * Queries the correct table based on role selected at login.
 */
public class AuthenticationService {

    private final Connection conn;

    public AuthenticationService() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public User authenticate(String email, String password, String role) {
        try {
            switch (role) {
                case "Student":   return authenticateStudent(email, password);
                case "Organizer": return authenticateOrganizer(email, password);
                case "Admin":     return authenticateAdmin(email, password);
                default:          return null;
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }

    public boolean register(String name, String email, String password, String role) {
        if (emailExists(email, role)) return false;
        try {
            switch (role) {
                case "Student":   return insertStudent(name, email, password);
                case "Organizer": return insertOrganizer(name, email, password);
                case "Admin":     return insertAdmin(name, email, password);
                default:          return false;
            }
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    private Student authenticateStudent(String email, String password) throws SQLException {
        String sql = "SELECT * FROM student WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Student(
                    rs.getInt("student_id"), rs.getString("name"),
                    rs.getString("email"),   rs.getString("password"),
                    rs.getInt("student_id"),
                    rs.getString("department") != null ? rs.getString("department") : "General"
                );
            }
        }
        return null;
    }

    private Organizer authenticateOrganizer(String email, String password) throws SQLException {
        String sql = "SELECT * FROM organizer WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Organizer(
                    rs.getInt("organizer_id"), rs.getString("name"),
                    rs.getString("email"),     rs.getString("password"),
                    rs.getInt("organizer_id"),
                    rs.getString("contact") != null ? rs.getString("contact") : ""
                );
            }
        }
        return null;
    }

    private Admin authenticateAdmin(String email, String password) throws SQLException {
        String sql = "SELECT * FROM admin WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                    rs.getInt("admin_id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("password"),
                    rs.getInt("admin_id")
                );
            }
        }
        return null;
    }

    private boolean insertStudent(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO student (name, email, password, department) VALUES (?, ?, ?, 'General')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, password);
            ps.executeUpdate(); return true;
        }
    }

    private boolean insertOrganizer(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO organizer (name, email, password, contact) VALUES (?, ?, ?, '')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, password);
            ps.executeUpdate(); return true;
        }
    }

    private boolean insertAdmin(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO admin (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, password);
            ps.executeUpdate(); return true;
        }
    }

    private boolean emailExists(String email, String role) {
        String table = role.equals("Student") ? "student"
                     : role.equals("Organizer") ? "organizer" : "admin";
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Email check error: " + e.getMessage());
        }
        return false;
    }
}