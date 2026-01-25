package view.Dialogs;

import database.TransactionDAO;
import model.Transaction;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BorrowedBooksDialog extends JDialog {

    public BorrowedBooksDialog(JFrame parent, User user, String type) {
        super(parent, type, true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(0x5A7863));
        JLabel title = new JLabel(type);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"Book Title", "Student", "Issue Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Load Data
        TransactionDAO dao = new TransactionDAO();
        List<Transaction> all = dao.getAllTransactions(); 
        
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        
        // Determine Student ID
        int tempStudentId = -1;
        if (!isAdmin) {
            try {
                tempStudentId = new database.UserDAO().getStudentByUserId(user.getId()).getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final int studentId = tempStudentId;

        for (Transaction t : all) {
            // Filter by student if not admin
            if (!isAdmin && t.getStudentId() != studentId) {
                continue;
            }
            
            // Calculate Overdue Status
            boolean isOverdue = false;
            if (t.getIssueDate() != null) {
                LocalDate issueDate = new java.sql.Date(t.getIssueDate().getTime()).toLocalDate();
                long daysBetween = ChronoUnit.DAYS.between(issueDate, LocalDate.now());
                if (daysBetween > 15) { // CHANGED: Limit set to 15 days
                    isOverdue = true;
                }
            }

            if ("My Borrowed Books".equals(type)) {
                // Show all history
                model.addRow(new Object[]{t.getBookTitle(), t.getStudentName(), t.getIssueDate(), t.getStatus()});
            } else if ("Overdue Books".equals(type)) {
                // Show only overdue issued books
                if ("ISSUED".equals(t.getStatus()) && isOverdue) {
                     model.addRow(new Object[]{t.getBookTitle(), t.getStudentName(), t.getIssueDate(), "OVERDUE"});
                }
            }
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel bottom = new JPanel();
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        
        add(bottom, BorderLayout.SOUTH);
    }
}