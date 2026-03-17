package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model class representing a registration.
 */
public class Registration implements Serializable {
    private int registrationId;
    private int studentId;
    private int eventId;
    private LocalDate registrationDate;

    public Registration() {}

    public Registration(int registrationId, int studentId, int eventId, LocalDate registrationDate) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Registration{" + "registrationId=" + registrationId + ", studentId=" + studentId + ", eventId=" + eventId + ", registrationDate=" + registrationDate + '}';
    }
}
