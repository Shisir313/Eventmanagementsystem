package model;

public class Student extends User {

    private int    studentId;
    private String department;

    public Student(int userId, String name, String email, String password,
                   int studentId, String department) {
        super(userId, name, email, password, "Student");
        this.studentId  = studentId;
        this.department = department;
    }

    @Override
    public void showDashboard() {
        System.out.println("Opening Student Dashboard for: " + getName());
    }

    public int    getStudentId()             { return studentId; }
    public String getDepartment()            { return department; }
    public void   setStudentId(int id)       { this.studentId = id; }
    public void   setDepartment(String dept) { this.department = dept; }

    @Override
    public String toString() {
        return "Student{id=" + studentId + ", name='" + getName() + "'}";
    }
}