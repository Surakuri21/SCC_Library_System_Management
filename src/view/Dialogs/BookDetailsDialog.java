package view.Dialogs;

import database.TransactionDAO;
import database.UserDAO;
import model.Books;
import model.Student;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BookDetailsDialog extends JDialog {

    public BookDetailsDialog(JFrame parent, User user, Books book) {
        super(parent, "Book Details", true);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        // Title
        JLabel lblTitle = new JLabel("<html><center>" + book.getTitle() + "</center></html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblTitle);
        
        content.add(Box.createVerticalStrut(10));
        
        // Author
        JLabel lblAuthor = new JLabel("by " + book.getAuthor());
        lblAuthor.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblAuthor.setForeground(Color.GRAY);
        lblAuthor.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblAuthor);
        
        content.add(Box.createVerticalStrut(20));
        
        // Details
        addDetail(content, "Category:", book.getCategory());
        addDetail(content, "ISBN:", book.getIsbn());
        addDetail(content, "Status:", book.getStatus());
        
        content.add(Box.createVerticalStrut(30));
        
        // Action Button
        String btnText = "ADMIN".equalsIgnoreCase(user.getRole()) ? "Issue Book" : "Reserve Book";
        Color btnColor = "ADMIN".equalsIgnoreCase(user.getRole()) ? new Color(0x4A7C59) : new Color(0x336699);
        
        JButton btnAction = new JButton(btnText);
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAction.setBackground(btnColor);
        btnAction.setForeground(Color.WHITE);
        btnAction.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Validation
        if (book.getQuantity() <= 0) {
            btnAction.setEnabled(false);
            btnAction.setText("Out of Stock");
            btnAction.setBackground(Color.LIGHT_GRAY);
        }
        
        btnAction.addActionListener(e -> {
            // Get Student ID
            int studentId = -1;
            
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                String input = JOptionPane.showInputDialog(this, "Enter Student DB ID to issue to:");
                if (input != null && !input.isEmpty()) {
                    try {
                        studentId = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid ID");
                        return;
                    }
                } else {
                    return; // Cancelled
                }
            } else {
                // Student
                UserDAO userDAO = new UserDAO();
                Student student = userDAO.getStudentByUserId(user.getId());
                if (student != null) {
                    studentId = student.getId();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: No Student Record found for this user.");
                    return;
                }
            }
            
            TransactionDAO tDAO = new TransactionDAO();
            
            // 1. Check Borrow Limit (Max 3 books)
            if (tDAO.hasReachedBorrowLimit(studentId)) {
                JOptionPane.showMessageDialog(this, "You reached a maximum borrow limit (3 books).", "Limit Reached", JOptionPane.WARNING_MESSAGE);
                return; // Stop execution, do not close dialog immediately so they see the warning
            }
            
            // 2. Check for Duplicates
            System.out.println("Checking duplicate for StudentID: " + studentId + ", BookID: " + book.getId());
            boolean isDuplicate = tDAO.isBookAlreadyBorrowed(studentId, book.getId());
            System.out.println("Is Duplicate? " + isDuplicate);
            
            if (isDuplicate) {
                JOptionPane.showMessageDialog(this, "You have already reserved/borrowed this book!", "Duplicate Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Perform Action
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                tDAO.issueBook(studentId, book.getId());
                JOptionPane.showMessageDialog(this, "Book Issued Successfully!");
            } else {
                tDAO.reserveBook(studentId, book.getId());
                JOptionPane.showMessageDialog(this, "Book Reserved! Please pick it up within 24 hours.");
            }
            
            dispose();
        });
        
        content.add(btnAction);
        
        add(content, BorderLayout.CENTER);
    }
    
    private void addDetail(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setBackground(Color.WHITE);
        row.add(new JLabel("<html><b>" + label + "</b> " + value + "</html>"));
        panel.add(row);
    }
}