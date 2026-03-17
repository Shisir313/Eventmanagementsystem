package service;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service responsible for authentication logic.
 * For simplicity, this example checks existence of ID in respective tables and
 * provides simple registration methods that insert into the DB and return the new ID.
 */
public class AuthenticationService {

    // store the last error message (if any) so UI can retrieve it for feedback
    private static String lastError = null;

    public static String getLastError() {
        return lastError;
    }

    /**
     * Register a new student. Returns generated student ID or -1 on failure.
     */
    public int registerStudent(String name, String email) {
        lastError = null;
        String sql = "INSERT INTO Student (StudentName, Email) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            lastError = ex.getMessage();
            return -1;
        }
    }

    /**
     * Register a new organizer. Returns generated organizer ID or -1 on failure.
     */
    public int registerOrganizer(String name, String contact) {
        lastError = null;
        String sql = "INSERT INTO Organizer (OrganizerName, Contact) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, contact);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            lastError = ex.getMessage();
            return -1;
        }
    }

    /**
     * Register a new admin. Returns generated admin ID or -1 on failure.
     */
    public int registerAdmin(String name, String email) {
        lastError = null;
        String sql = "INSERT INTO Admin (AdminName, Email) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            lastError = ex.getMessage();
            return -1;
        }
    }

    /**
     * Check student login by studentId (could be expanded to username/password)
     */
    public boolean loginStudent(int studentId) {
        lastError = null;
        String sql = "SELECT StudentID FROM Student WHERE StudentID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lastError = ex.getMessage();
            return false;
        }
    }

    /**
     * Check organizer login by organizerId
     */
    public boolean loginOrganizer(int organizerId) {
        lastError = null;
        String sql = "SELECT OrganizerID FROM Organizer WHERE OrganizerID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, organizerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lastError = ex.getMessage();
            return false;
        }
    }

    /**
     * Check admin login by adminId
     */
    public boolean loginAdmin(int adminId) {
        lastError = null;
        String sql = "SELECT AdminID FROM Admin WHERE AdminID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lastError = ex.getMessage();
            return false;
        }
    }
}