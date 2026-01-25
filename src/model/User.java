package model;

public class User {
    private int id; // Added ID
    private String username;
    private String password;
    private String role; 

    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Constructor for backward compatibility (Kung Kinakailangan)
    public User(String username, String password, String role) {
        this(-1, username, password, role);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}