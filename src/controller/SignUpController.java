package controller;

import database.UserDAO;
import view.Authentication.SignUpScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpController {
    private SignUpScreen view;
    private UserDAO userDAO;
    
    // The Secret Key to become an Admin
    private static final String ADMIN_SECRET_KEY = "SCC_ADMIN_2026";

    public SignUpController(SignUpScreen view) {
        this.view = view;
        this.userDAO = new UserDAO();
        
        this.view.getSignUpButton().addActionListener(new SignUpListener());
        this.view.getCancelButton().addActionListener(e -> view.dispose());
        

        
        this.view.getRootPane().setDefaultButton(this.view.getSignUpButton());
    }

    class SignUpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();
            String confirmPass = view.getConfirmPassword();
            String secretKey = view.getSecretKey();
            
            // New Fields
            String studentId = view.getStudentId();
            String fullName = view.getFullName();
            String course = view.getCourse();
            String yearLevelStr = view.getYearLevel();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please fill in Username and Password.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirmPass)) {
                JOptionPane.showMessageDialog(view, "Passwords do not match.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Determine Role
            String role = "STUDENT";
            if (!secretKey.isEmpty()) {
                if (ADMIN_SECRET_KEY.equals(secretKey)) {
                    role = "ADMIN";
                } else {
                    JOptionPane.showMessageDialog(view, "Invalid Admin Secret Key!", "Security Warning", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
            }
            
            int yearLevel = 0;
            // Validation for Students
            if ("STUDENT".equals(role)) {
                if (studentId.isEmpty() || fullName.isEmpty() || course.isEmpty() || yearLevelStr.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Students must provide all details (ID, Name, Course, Year).", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    yearLevel = Integer.parseInt(yearLevelStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Year Level must be a number.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            final String finalRole = role;
            final int finalYearLevel = yearLevel;

            // Run in background
            new Thread(() -> {
                // Pass all details to DAO
                boolean success = userDAO.registerUser(username, password, finalRole, studentId, fullName, course, finalYearLevel);
                
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        String msg = "Account created successfully!";
                        if ("ADMIN".equals(finalRole)) {
                            msg += "\nWelcome, Administrator.";
                        }
                        JOptionPane.showMessageDialog(view, msg);
                        view.dispose();
                    } else {
                        if (userDAO.isUsernameTaken(username)) {
                            JOptionPane.showMessageDialog(view, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(view, "Registration failed. Database error.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }).start();
        }
    }
}