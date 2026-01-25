import controller.LoginController;
import view.Authentication.LoginScreen;

import javax.swing.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("Starting Application...");
                    LoginScreen loginView = new LoginScreen();
                    new LoginController(loginView); // Connecting the  View and Controller
                    loginView.setVisible(true);
                    System.out.println("Login Screen Visible");
                } catch (Exception e) {
                    logError(e);
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error starting app: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            logError(e);
            e.printStackTrace();
        }
    }

    private static void logError(Exception e) {
        try (FileWriter fw = new FileWriter("error_log.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("--- Error at " + java.time.LocalDateTime.now() + " ---");
            e.printStackTrace(pw);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}