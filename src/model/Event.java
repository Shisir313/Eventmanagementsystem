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
    private String location;
    private String description;
    private String status;

    public Event() {}

    public Event(int eventId, String eventName, LocalDate eventDate, String location, String description, int organizerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.location = location;
        this.description = description;
        this.organizerId = organizerId;
        this.status = "Pending";
    }

    // Optional constructor including status
    public Event(int eventId, String eventName, LocalDate eventDate, String location, String description, int organizerId, String status) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.location = location;
        this.description = description;
        this.organizerId = organizerId;
        this.status = status != null ? status : "Pending";
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    @Override
    public String toString() {
        return "Event{" + "eventId=" + eventId + ", eventName='" + eventName + '\'' + ", eventDate=" + eventDate + ", location='" + location + '\'' + ", description='" + description + '\'' + ", organizerId=" + organizerId + ", status=" + status + '}';
    }
}