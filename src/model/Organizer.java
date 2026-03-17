package model;

import java.io.Serializable;

/**
 * Model class representing an organizer.
 */
public class Organizer implements Serializable {
    private int organizerId;
    private String organizerName;
    private String contact;

    public Organizer() {}

    public Organizer(int organizerId, String organizerName, String contact) {
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.contact = contact;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Organizer{" + "organizerId=" + organizerId + ", organizerName='" + organizerName + '\'' + ", contact='" + contact + '\'' + '}';
    }
}
