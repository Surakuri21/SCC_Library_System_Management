package controller;

import database.UserDAO;
import model.User;
import view.Authentication.LoginScreen;
import view.Dashboard_Panel.MainDashboardScreen;
import view.Authentication.SignUpScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginScreen view;
    private UserDAO userDAO;

    public LoginController(LoginScreen view) {
        this.view = view;
        this.userDAO = new UserDAO(); 
        this.view.getLoginButton().addActionListener(new LoginListener());
        this.view.getSignUpButton().addActionListener(e -> openSignUp());
        
        this.view.getUsernameField().addActionListener(e -> this.view.getPasswordField().requestFocus());
        this.view.getPasswordField().addActionListener(new LoginListener());
    }

    private void openSignUp() {
        SignUpScreen signUpView = new SignUpScreen();
        new SignUpController(signUpView);
        signUpView.setVisible(true);
    }

    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }


            new Thread(() -> {
                User user = userDAO.authenticateUser(username, password);
                
                SwingUtilities.invokeLater(() -> {
                    if (user != null) {
                        JOptionPane.showMessageDialog(view, "Login  Successfully :) ");
                        view.dispose();
                        try {
                            MainDashboardScreen dashboard = new MainDashboardScreen(user);
                            dashboard.setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        // PRODUCTION MODE: Generic Error Message
                        JOptionPane.showMessageDialog(view, 
                            "Invalid username or password.", 
                            "Login Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
        }
    }
}