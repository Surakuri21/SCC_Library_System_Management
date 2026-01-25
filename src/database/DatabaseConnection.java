package database;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static String lastError = "";

    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DB_NAME = "scc_library_db";
    private static final String PARAMS = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Testing Area

    private DatabaseConnection() {
        try {

            System.out.println("--- Debugging Classpath ---");
            String classpath = System.getProperty("java.class.path");
            System.out.println(classpath);
            System.out.println("---------------------------");

            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (!connect("localhost", 3306)) {
                connect("127.0.0.1", 3306);
            }
            
        } catch (ClassNotFoundException e) {
            lastError = "MySQL JDBC Driver not found.";
            System.err.println("Error: " + lastError);
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null, 
                "CRITICAL ERROR: MySQL Driver Missing!\n\n" +
                "Please add 'mysql-connector-j-8.x.x.jar' to your project libraries.\n" +
                "1. Create a 'lib' folder.\n" +
                "2. Paste the jar file there.\n" +
                "3. Right-click -> Add as Library.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean connect(String host, int port) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + DB_NAME + PARAMS;
        try {
            this.connection = DriverManager.getConnection(url, USER, PASSWORD);
            System.out.println("Database connected successfully to " + host);
            lastError = ""; 
            return true;
        } catch (SQLException e) {
            lastError = "Could not connect to " + host + ": " + e.getMessage();
            System.err.println("Error: " + lastError);
            return false;
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                if (instance.getConnection() == null || instance.getConnection().isClosed()) {
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getLastError() {
        return lastError;
    }

    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        DatabaseConnection.getInstance();
    }
}