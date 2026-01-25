package view.Dashboard_Panel;

import database.AnnouncementDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AnnouncementManagementPanel extends JPanel {

    // --- THEME COLORS (Copied from BookManagementPanel) ---
    private static final Color BG_CREAM = new Color(0xEB, 0xF4, 0xDD);
    private static final Color SAGE_GREEN = new Color(0x90, 0xAB, 0x8B);
    private static final Color FOREST_GREEN = new Color(0x5A, 0x78, 0x63);
    private static final Color DARK_SLATE = new Color(0x3B, 0x49, 0x53);
    private static final Color PURE_WHITE = Color.WHITE;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    private JTextField txtTitle;
    private JTextArea txtContent;
    private JComboBox<String> cmbType;

    public AnnouncementManagementPanel(User user) {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_CREAM); // Applied Theme Color
        setBorder(new EmptyBorder(30, 40, 30, 40)); // Applied Theme Padding

        // Header
        JLabel titleLabel = new JLabel("Manage Announcements");
        titleLabel.setFont(TITLE_FONT); // Applied Theme Font
        titleLabel.setForeground(DARK_SLATE); // Applied Theme Color
        add(titleLabel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(PURE_WHITE);
        // Applied Theme Border
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SAGE_GREEN, 1, true),
                new EmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;

        // Title Input
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(createLabel("Announcement Title"), gbc);

        gbc.gridy = 1;
        txtTitle = new JTextField();
        txtTitle.setPreferredSize(new Dimension(0, 40));
        txtTitle.setFont(MAIN_FONT);
        // Applied Theme Border to Input
        txtTitle.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SAGE_GREEN, 1, true), 
                new EmptyBorder(5, 10, 5, 10) 
        ));
        contentPanel.add(txtTitle, gbc);

        // Type Input
        gbc.gridy = 2;
        contentPanel.add(createLabel("Type"), gbc);

        gbc.gridy = 3;
        String[] types = {"General", "Urgent", "Event", "Maintenance"};
        cmbType = new JComboBox<>(types);
        cmbType.setPreferredSize(new Dimension(0, 40));
        cmbType.setFont(MAIN_FONT);
        cmbType.setBackground(PURE_WHITE);
        contentPanel.add(cmbType, gbc);

        // Content Input
        gbc.gridy = 4;
        contentPanel.add(createLabel("Message Content"), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0; // Give vertical space to text area
        gbc.fill = GridBagConstraints.BOTH;
        txtContent = new JTextArea();
        txtContent.setFont(MAIN_FONT);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        // Applied Theme Border to TextArea
        txtContent.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(txtContent);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        scrollPane.setBorder(new LineBorder(SAGE_GREEN, 1, true)); // Applied Theme Border
        contentPanel.add(scrollPane, gbc);

        // Post Button
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        
        JButton btnPost = new JButton("Post Announcement");
        btnPost.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPost.setForeground(PURE_WHITE);
        btnPost.setBackground(FOREST_GREEN); // Applied Theme Color
        btnPost.setFocusPainted(false);
        btnPost.setBorderPainted(false);
        btnPost.setPreferredSize(new Dimension(200, 45));
        btnPost.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPost.addActionListener(e -> postAnnouncement());
        
        contentPanel.add(btnPost, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK_SLATE); // Applied Theme Color
        return label;
    }

    private void postAnnouncement() {
        String title = txtTitle.getText().trim();
        String content = txtContent.getText().trim();
        String type = (String) cmbType.getSelectedItem();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both Title and Content.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new AnnouncementDAO().addAnnouncement(title, content, type);
        
        JOptionPane.showMessageDialog(this, "Announcement Posted Successfully!");
        txtTitle.setText("");
        txtContent.setText("");
        cmbType.setSelectedIndex(0);
    }
}