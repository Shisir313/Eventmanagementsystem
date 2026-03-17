package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model class representing an event.
 */
public class Event implements Serializable {
    private int eventId;
    private String eventName;
    private LocalDate eventDate;
    private int organizerId;

    public Event() {}

    public Event(int eventId, String eventName, LocalDate eventDate, int organizerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.organizerId = organizerId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    @Override
    public String toString() {
        return "Event{" + "eventId=" + eventId + ", eventName='" + eventName + '\'' + ", eventDate=" + eventDate + ", organizerId=" + organizerId + '}';
    }
}
