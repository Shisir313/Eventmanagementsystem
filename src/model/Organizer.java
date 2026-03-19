package model;

public class Organizer extends User {

    private int    organizerId;
    private String contact;

    public Organizer(int userId, String name, String email, String password,
                     int organizerId, String contact) {
        super(userId, name, email, password, "Organizer");
        this.organizerId = organizerId;
        this.contact     = contact;
    }

    @Override
    public void showDashboard() {
        System.out.println("Opening Organizer Dashboard for: " + getName());
    }

    public int    getOrganizerId()              { return organizerId; }
    public String getContact()                  { return contact; }
    public void   setOrganizerId(int id)        { this.organizerId = id; }
    public void   setContact(String contact)    { this.contact = contact; }

    @Override
    public String toString() {
        return "Organizer{id=" + organizerId + ", name='" + getName() + "'}";
    }
}