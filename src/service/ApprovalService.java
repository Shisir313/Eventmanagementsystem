package service;

import database.DBConnection;
import model.Approval;
import java.sql.*;
import java.util.*;

public class ApprovalService {

    private final Connection conn;
    private final RegistrationService registrationService;

    public ApprovalService() {
        this.conn                = DBConnection.getInstance().getConnection();
        this.registrationService = new RegistrationService();
    }

    // adminId removed — approvals no longer record which admin approved
    public boolean approve(int registrationId) {
        return processDecision(registrationId, "APPROVED", "Approved");
    }

    public boolean reject(int registrationId, String remarks) {
        return processDecision(registrationId, "REJECTED", remarks);
    }

    private boolean processDecision(int registrationId, String status, String remarks) {
        // Removed admin_id column from insert
        String sql = "INSERT INTO approval (registration_id, status, remarks) VALUES (?, ?, ?)";
        boolean originalAutoCommit = true;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ps.setInt(1, registrationId);
            ps.setString(2, status);      ps.setString(3, remarks);
            int inserted = ps.executeUpdate();

            // Update registration status
            boolean updated = registrationService.updateStatus(registrationId, status);

            if (inserted > 0 && updated) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("Approval error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) {}
        }
    }
}