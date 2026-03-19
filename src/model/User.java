package model;

/**
 * Abstract base class representing any system user.
 *
 * OOP Concepts demonstrated:
 *  - Abstraction   : declared abstract — cannot be instantiated directly
 *  - Encapsulation : all fields are private, exposed via getters/setters
 *  - Inheritance   : Student, Organizer, Admin all extend this class
 *  - Polymorphism  : showDashboard() is overridden by each subclass
 */
public abstract class User {

    private int    userId;
    private String name;
    private String email;
    private String password;
    private String role;

    public User(int userId, String name, String email, String password, String role) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    /** Each subclass MUST implement — enforces Abstraction + Polymorphism */
    public abstract void showDashboard();

    public void logout() {
        System.out.println(name + " (" + role + ") has logged out.");
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int    getUserId()            { return userId; }
    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public String getPassword()         { return password; }
    public String getRole()             { return role; }
    public void   setUserId(int id)     { this.userId = id; }
    public void   setName(String n)     { this.name = n; }
    public void   setEmail(String e)    { this.email = e; }
    public void   setPassword(String p) { this.password = p; }
    public void   setRole(String r)     { this.role = r; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", name='" + name + "', role='" + role + "'}";
    }
}