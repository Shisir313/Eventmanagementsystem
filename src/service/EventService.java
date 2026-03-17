package service;

import database.DBConnection;
import model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for event-related operations.
 */
public class EventService {

    /**
     * Create a new event. Returns generated event id or -1 on failure.
     */
    public int createEvent(Event event) {
        String sql = "INSERT INTO Event (EventName, EventDate, OrganizerID) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getEventName());
            ps.setDate(2, java.sql.Date.valueOf(event.getEventDate()));
            ps.setInt(3, event.getOrganizerId());
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
     * Update an existing event. Returns true on success.
     */
    public boolean updateEvent(Event event) {
        String sql = "UPDATE Event SET EventName = ?, EventDate = ? WHERE EventID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getEventName());
            ps.setDate(2, java.sql.Date.valueOf(event.getEventDate()));
            ps.setInt(3, event.getEventId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieve all events from database.
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT EventID, EventName, EventDate, OrganizerID FROM Event";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Event e = new Event();
                e.setEventId(rs.getInt("EventID"));
                e.setEventName(rs.getString("EventName"));
                e.setEventDate(rs.getDate("EventDate").toLocalDate());
                e.setOrganizerId(rs.getInt("OrganizerID"));
                events.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }
}
