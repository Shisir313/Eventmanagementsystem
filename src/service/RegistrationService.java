package service;

import database.DBConnection;
import model.Registration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Service to handle event registrations.
 */
public class RegistrationService {

    /**
     * Register a student for an event. Returns registration ID or -1 on failure.
     */
    public int registerStudentForEvent(Registration reg) {
        if (!validateRegistration(reg)) {
            return -1;
        }
        String sql = "INSERT INTO Registration (StudentID, EventID, RegistrationDate) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reg.getStudentId());
            ps.setInt(2, reg.getEventId());
            ps.setDate(3, java.sql.Date.valueOf(reg.getRegistrationDate()));
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * Basic validation: check if same student is already registered for same event.
     */
    public boolean validateRegistration(Registration reg) {
        String sql = "SELECT RegistrationID FROM Registration WHERE StudentID = ? AND EventID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reg.getStudentId());
            ps.setInt(2, reg.getEventId());
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next(); // valid if no existing registration
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
