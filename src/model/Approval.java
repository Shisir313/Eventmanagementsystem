package model;

import java.io.Serializable;

/**
 * Model class representing an approval record.
 */
public class Approval implements Serializable {
    private int approvalId;
    private int registrationId;
    private String status; // e.g., PENDING, APPROVED, REJECTED

    public Approval() {}

    public Approval(int approvalId, int registrationId, String status) {
        this.approvalId = approvalId;
        this.registrationId = registrationId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Approval{" + "approvalId=" + approvalId + ", registrationId=" + registrationId + ", status='" + status + '\'' + '}';
    }
}