package model;

public class Admin extends User {

    private int adminId;

    public Admin(int userId, String name, String email, String password, int adminId) {
        super(userId, name, email, password, "Admin");
        this.adminId = adminId;
    }

    @Override
    public void showDashboard() {
        System.out.println("Opening Admin Dashboard for: " + getName());
    }

    public int  getAdminId()        { return adminId; }
    public void setAdminId(int id)  { this.adminId = id; }

    @Override
    public String toString() {
        return "Admin{id=" + adminId + ", name='" + getName() + "'}";
    }
}
