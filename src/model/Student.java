package model;

public class Student {
    private int id;
    private String studentId;
    private String name;
    private String course;
    private int yearLevel;

    public Student(int id, String studentId, String name, String course, int yearLevel) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.yearLevel = yearLevel;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getYearLevel() { return yearLevel; }
    public void setYearLevel(int yearLevel) { this.yearLevel = yearLevel; }
}