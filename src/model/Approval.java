package model;

import java.io.Serializable;

/**
 * Model class representing an approval record.
 */
public class Approval implements Serializable {
    private int approvalId;
    private int registrationId;
    private int adminId;
    private String status; // e.g., PENDING, APPROVED, REJECTED

    public Approval() {}

    public Approval(int approvalId, int registrationId, int adminId, String status) {
        this.approvalId = approvalId;
        this.registrationId = registrationId;
        this.adminId = adminId;
        this.status = status;
    }

    public int getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(int approvalId) {
        this.approvalId = approvalId;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Approval{" + "approvalId=" + approvalId + ", registrationId=" + registrationId + ", adminId=" + adminId + ", status='" + status + '\'' + '}';
    }
}
