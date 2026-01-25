package view.Dialogs;

import model.Books;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DeleteConfirmationDialog extends JDialog {

    private boolean confirmed = false;
    private static final Color BG_CREAM = new Color(0xEB, 0xF4, 0xDD);
    private static final Color ALERT_RED = new Color(0xD9, 0x53, 0x4F);
    private static final Color DARK_SLATE = new Color(0x3B, 0x49, 0x53);

    public DeleteConfirmationDialog(JFrame parent, List<Books> booksToDelete) {
        super(parent, "Confirm Deletion", true);
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_CREAM);

        // Header
        JLabel lblHeader = new JLabel("<html>Are you sure you want to delete <br>the following " + booksToDelete.size() + " book(s)?</html>");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(DARK_SLATE);
        lblHeader.setBorder(new EmptyBorder(20, 20, 10, 20));
        add(lblHeader, BorderLayout.NORTH);

        // List Panel (Card style)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        for (Books book : booksToDelete) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setBorder(new EmptyBorder(10, 10, 10, 10));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            
            JLabel lblTitle = new JLabel(book.getTitle());
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblTitle.setForeground(DARK_SLATE);
            
            JLabel lblAuthor = new JLabel("by " + book.getAuthor());
            lblAuthor.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblAuthor.setForeground(Color.GRAY);
            
            row.add(lblTitle, BorderLayout.NORTH);
            row.add(lblAuthor, BorderLayout.CENTER);
            
            listPanel.add(row);
            listPanel.add(new JSeparator());
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Warning Text
        JLabel lblWarning = new JLabel("<html><center>This action cannot be undone.</center></html>");
        lblWarning.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWarning.setForeground(ALERT_RED);
        lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
        lblWarning.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_CREAM);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(BG_CREAM);
        southPanel.add(lblWarning, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        JButton btnDelete = new JButton("Delete");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setBackground(ALERT_RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);

        add(southPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}