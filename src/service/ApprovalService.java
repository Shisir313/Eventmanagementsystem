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

    public boolean approve(int registrationId, int adminId) {
        return processDecision(registrationId, adminId, "Approved", "Approved by admin");
    }

    public boolean reject(int registrationId, int adminId, String remarks) {
        return processDecision(registrationId, adminId, "Rejected", remarks);
    }

    private boolean processDecision(int registrationId, int adminId, String status, String remarks) {
        String sql = "INSERT INTO approval (registration_id, admin_id, status, remarks) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId); ps.setInt(2, adminId);
            ps.setString(3, status);      ps.setString(4, remarks);
            ps.executeUpdate();
            registrationService.updateStatus(registrationId, status);
            return true;
        } catch (SQLException e) {
            System.err.println("Approval error: " + e.getMessage()); return false;
        }
    }
}