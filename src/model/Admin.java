package model;

import java.io.Serializable;

/**
 * Model class representing an admin.
 */
public class Admin implements Serializable {
    private int adminId;
    private String adminName;
    private String email;

    public Admin() {}

    public Admin(int adminId, String adminName, String email) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.email = email;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Admin{" + "adminId=" + adminId + ", adminName='" + adminName + '\'' + ", email='" + email + '\'' + '}';
    }
}
