package service;

import database.DBConnection;
import model.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    private final Connection conn;

    public EventService() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean createEvent(Event event) {
        String sql = "INSERT INTO event (event_name, event_date, location, description, organizer_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getEventName());
            ps.setDate(2, java.sql.Date.valueOf(event.getEventDate()));
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getDescription());
            ps.setInt(5, event.getOrganizerId());
            ps.executeUpdate(); return true;
        } catch (SQLException e) {
            System.err.println("Create event error: " + e.getMessage()); return false;
        }
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM event ORDER BY event_date ASC")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Get events error: " + e.getMessage()); }
        return list;
    }

    public List<Event> getEventsByOrganizer(int organizerId) {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM event WHERE organizer_id = ? ORDER BY event_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, organizerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Get organizer events error: " + e.getMessage()); }
        return list;
    }

    public boolean updateEvent(Event event) {
        String sql = "UPDATE event SET event_name=?, event_date=?, location=?, description=? WHERE event_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getEventName());
            ps.setDate(2, java.sql.Date.valueOf(event.getEventDate()));
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getDescription());
            ps.setInt(5, event.getEventId());
            ps.executeUpdate(); return true;
        } catch (SQLException e) {
            System.err.println("Update event error: " + e.getMessage()); return false;
        }
    }

    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM event WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId); ps.executeUpdate(); return true;
        } catch (SQLException e) {
            System.err.println("Delete event error: " + e.getMessage()); return false;
        }
    }

    // New: allow admins to change an event's status (e.g., APPROVED / REJECTED)
    public boolean updateStatus(int eventId, String status) {
        String sql = "UPDATE event SET status = ? WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, eventId);
            int rows = ps.executeUpdate();
            System.out.println("updateEventStatus: rowsAffected=" + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Update event status error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Optional: helper to fetch only pending events
    public List<Event> getAllPendingEvents() {
        List<Event> list = new ArrayList<>();
        // Use case-insensitive comparison to tolerate 'Pending'/'PENDING' defaults
        String sql = "SELECT * FROM event WHERE UPPER(status) = 'PENDING' ORDER BY event_date ASC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Get pending events error: " + e.getMessage()); }
        return list;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event(
            rs.getInt("event_id"), rs.getString("event_name"),
            rs.getDate("event_date").toLocalDate(),
            rs.getString("location"), rs.getString("description"),
            rs.getInt("organizer_id")
        );
        e.setStatus(rs.getString("status"));
        return e;
    }
}