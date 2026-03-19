package service;

import database.DBConnection;
import model.Registration;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistrationService {

    private final Connection conn;

    public RegistrationService() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean registerStudent(int studentId, int eventId) {
        if (isAlreadyRegistered(studentId, eventId)) return false;
        // Register as PENDING — admin must approve via ApprovalService
        String sql = "INSERT INTO registration (student_id, event_id, registration_date, status) " +
                     "VALUES (?, ?, ?, 'PENDING')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId); ps.setInt(2, eventId);
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            int rows = ps.executeUpdate();
            System.out.println("registerStudent: executed insert, rowsAffected=" + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Registration> getRegistrationsByStudent(int studentId) {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT * FROM registration WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Get registrations error: " + e.getMessage()); e.printStackTrace(); }
        return list;
    }

    public List<Registration> getAllPendingRegistrations() {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT * FROM registration WHERE status = 'PENDING'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Get pending error: " + e.getMessage()); e.printStackTrace(); }
        return list;
    }

    public boolean updateStatus(int registrationId, String status) {
        String sql = "UPDATE registration SET status = ? WHERE registration_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, registrationId);
            int rows = ps.executeUpdate();
            System.out.println("updateStatus: rowsAffected=" + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Update status error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelRegistration(int registrationId) {
        String sql = "DELETE FROM registration WHERE registration_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId); int rows = ps.executeUpdate();
            System.out.println("cancelRegistration: rowsAffected=" + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Cancel error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAlreadyRegistered(int studentId, int eventId) {
        String sql = "SELECT COUNT(*) FROM registration WHERE student_id = ? AND event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { System.err.println("Duplicate check error: " + e.getMessage()); e.printStackTrace(); }
        return false;
    }

    /**
     * Helper: fetch a student's name by id. Returns null if not found or on error.
     */
    public String getStudentName(int studentId) {
        String sql = "SELECT name FROM student WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            System.err.println("Get student name error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Registration mapRow(ResultSet rs) throws SQLException {
        Registration r = new Registration(
            rs.getInt("registration_id"), rs.getInt("student_id"),
            rs.getInt("event_id"), rs.getDate("registration_date").toLocalDate()
        );
        r.setStatus(rs.getString("status"));
        return r;
    }
}