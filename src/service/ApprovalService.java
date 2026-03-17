package service;

import database.DBConnection;
import model.Approval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service to handle approvals of registrations.
 */
public class ApprovalService {
    private NotificationService notificationService = new NotificationService();

    /**
     * Approve a registration: insert an approval record and notify student.
     */
    public boolean approveRegistration(int registrationId, int adminId) {
        String insert = "INSERT INTO Approval (RegistrationID, AdminID, Status) VALUES (?, ?, 'APPROVED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, registrationId);
            ps.setInt(2, adminId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                // notify (in real app you'd lookup student contact/email)
                notificationService.sendNotification("Registration " + registrationId + " approved by admin " + adminId);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Reject a registration: insert an approval record with REJECTED status.
     */
    public boolean rejectRegistration(int registrationId, int adminId) {
        String insert = "INSERT INTO Approval (RegistrationID, AdminID, Status) VALUES (?, ?, 'REJECTED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, registrationId);
            ps.setInt(2, adminId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                notificationService.sendNotification("Registration " + registrationId + " rejected by admin " + adminId);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieve pending registrations - simple example that returns registration ids
     */
    public ResultSet getPendingRegistrations() throws SQLException {
        String sql = "SELECT r.RegistrationID, r.StudentID, r.EventID, r.RegistrationDate FROM Registration r LEFT JOIN Approval a ON r.RegistrationID = a.RegistrationID WHERE a.ApprovalID IS NULL";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.executeQuery();
    }
}
