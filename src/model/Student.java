package model;

import java.io.Serializable;

/**
 * Model class representing a student.
 */
public class Student implements Serializable {
    private int studentId;
    private String studentName;
    private String email;

    public Student() {}

    public Student(int studentId, String studentName, String email) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Student{" + "studentId=" + studentId + ", studentName='" + studentName + '\'' + ", email='" + email + '\'' + '}';
    }
}
