package database;

import model.Student;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public User authenticateUser(String username, String password) {
        if (connection == null) {
            System.err.println("DAO Error: Connection is null!");
            return null;
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        username, 
                        password, 
                        rs.getString("role")
                    );
                } else {
                    System.out.println("Authentication Failed: User not found or password incorrect for '" + username + "'");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        return null; 
    }

    public Student getStudentByUserId(int userId) {
        String query = "SELECT * FROM students WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getInt("year_level")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean registerUser(String username, String password, String role, String studentId, String fullName, String course, int yearLevel) {
        if (connection == null) return false;

        if (isUsernameTaken(username)) {
            System.out.println("DEBUG: Username taken: " + username);
            return false;
        }

        try {
            connection.setAutoCommit(false); // Start Transaction

            // 1. Insert User
            String userQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            int userId = -1;
            
            try (PreparedStatement pstmt = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role);
                int affected = pstmt.executeUpdate();
                System.out.println("DEBUG: User Inserted. Rows: " + affected);
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        System.out.println("DEBUG: Generated User ID: " + userId);
                    }
                }
            }

            // 2. If Student, Insert into Students table linked to User
            if ("STUDENT".equals(role) && userId != -1) {
                if (studentId == null || studentId.isEmpty()) {
                    System.err.println("DEBUG: Student ID is missing! Skipping student insert.");
                } else {
                    String studentQuery = "INSERT INTO students (student_id, name, course, year_level, user_id) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(studentQuery)) {
                        pstmt.setString(1, studentId);
                        pstmt.setString(2, fullName);
                        pstmt.setString(3, course);
                        pstmt.setInt(4, yearLevel);
                        pstmt.setInt(5, userId);
                        int rows = pstmt.executeUpdate();
                        System.out.println("DEBUG: Student Inserted. Rows: " + rows);
                    }
                }
            }

            connection.commit(); // Commit Transaction
            connection.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            System.err.println("DEBUG: Registration Failed! Rolling back.");
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean registerUser(String username, String password, String role) {
        return registerUser(username, password, role, null, null, null, 0);
    }

    public boolean isUsernameTaken(String username) {
        String query = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}