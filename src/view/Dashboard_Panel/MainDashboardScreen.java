package view.Dashboard_Panel;

import controller.LoginController;
import model.User;
import view.Authentication.LoginScreen;
import view.Style.StyleConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDashboardScreen extends JFrame {

    private final User currentUser;
    private final JPanel contentArea;
    private final CardLayout cardLayout;

    // Sidebar Buttons storage to handle "Active" state highlighting
    private JButton btnDashboard, btnBooks, btnMembers, btnTrans, btnReports, btnAnnouncements;

    public MainDashboardScreen(User user) {
        this.currentUser = user;

        // 1. Basic Frame Settings
        setTitle("Library System - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800); // Widescreen for the dashboard
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Initialize Sidebar (The Green Panel)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 3. Initialize Content Area (The Center)
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(StyleConfig.COLOR_MAIN_BG);

        // --- ADD YOUR VIEWS HERE ---

        // View 1: Dashboard (We will fix this file next)
        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            contentArea.add(new StudentHomePanel(user), "DASHBOARD");
        } else {
            contentArea.add(new AdminHomePanel(user), "DASHBOARD");
        }

        // View 2: Books (Your existing panel)
        contentArea.add(new BookManagementPanel(user), "BOOKS");

        // View 3: Members (Placeholder or your StudentRecordsPanel)
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            contentArea.add(new StudentRecordsPanel(), "MEMBERS");
            contentArea.add(new IssueReturnPanel(), "TRANSACTIONS");
            contentArea.add(new ReportsPanel(), "REPORTS");
            contentArea.add(new AnnouncementManagementPanel(user), "ANNOUNCEMENTS");
        }

        add(contentArea, BorderLayout.CENTER);

        // Default View
        cardLayout.show(contentArea, "DASHBOARD");
        highlightButton(btnDashboard); // Set dashboard as active
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(StyleConfig.COLOR_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0)); // Fixed Width
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // -- 1. Logo Section --
        // Load the logo image
        ImageIcon logoIcon = new ImageIcon("C:/Users/Adria/IntelliJ_Workspace/SCC_Library_System_Management/src/util/logo.png"); // Replace with your actual path
        Image img = logoIcon.getImage();
        Image newImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize to 100x100
        logoIcon = new ImageIcon(newImg);

        JLabel logoLabel = new JLabel("SCC LIBRARY SYSTEM 2026");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setIcon(logoIcon); // Set the icon
        logoLabel.setHorizontalTextPosition(JLabel.CENTER); // Text below icon
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Center in sidebar
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center text horizontally

        sidebar.add(logoLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30))); // Spacing

        // -- 2. Navigation Buttons --
        // Create buttons (All Left Aligned)
        btnDashboard = createNavButton("Dashboard", "DASHBOARD");
        btnBooks = createNavButton("Books", "BOOKS");
        
        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnBooks);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            btnMembers = createNavButton("Members", "MEMBERS");
            btnTrans = createNavButton("Transactions", "TRANSACTIONS");
            btnReports = createNavButton("Reports", "REPORTS");
            btnAnnouncements = createNavButton("Announcements", "ANNOUNCEMENTS");
            
            sidebar.add(btnMembers);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(btnTrans);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(btnReports);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(btnAnnouncements);
        }

        // -- 3. Bottom User Section --
        sidebar.add(Box.createVerticalGlue()); // Pushes everything down

        // Log out button styled like nav buttons but red
        JButton logoutBtn = createNavButton("Logout", "LOGOUT");
        logoutBtn.setForeground(new Color(255, 100, 100)); // Red text
        
        // Override action listener for logout
        for(java.awt.event.ActionListener al : logoutBtn.getActionListeners()) {
            logoutBtn.removeActionListener(al);
        }
        logoutBtn.addActionListener(e -> {
            dispose();
            LoginScreen loginView = new LoginScreen();
            new LoginController(loginView);
            loginView.setVisible(true);
        });

        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(StyleConfig.FONT_SIDEBAR);
        btn.setForeground(Color.WHITE);
        btn.setBackground(StyleConfig.COLOR_SIDEBAR_BG);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        btn.setFocusPainted(false);
        
        // FORCE LEFT ALIGNMENT
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btn.setMaximumSize(new Dimension(220, 45)); // Reverted button width
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover & Click Events
        btn.addActionListener(e -> {
            cardLayout.show(contentArea, cardName);
            resetAllButtons();
            highlightButton(btn);
        });

        // Clean Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if(btn.getBackground() != StyleConfig.COLOR_ACCENT_BG)
                    btn.setBackground(StyleConfig.COLOR_SIDEBAR_BG.brighter());
            }
            public void mouseExited(MouseEvent e) {
                if(btn.getBackground() != StyleConfig.COLOR_ACCENT_BG)
                    btn.setBackground(StyleConfig.COLOR_SIDEBAR_BG);
            }
        });

        return btn;
    }

    private void highlightButton(JButton btn) {
        if(btn == null) return;
        btn.setBackground(StyleConfig.COLOR_ACCENT_BG); // The "Pine Glade" color
        btn.setForeground(StyleConfig.COLOR_TEXT_HEADER); // Dark text on light green
        btn.setFont(StyleConfig.FONT_SIDEBAR_BOLD);
    }

    private void resetAllButtons() {
        resetButton(btnDashboard);
        resetButton(btnBooks);
        resetButton(btnMembers);
        resetButton(btnTrans);
        resetButton(btnReports);
        resetButton(btnAnnouncements);
    }

    private void resetButton(JButton btn) {
        if(btn == null) return;
        btn.setBackground(StyleConfig.COLOR_SIDEBAR_BG);
        btn.setForeground(Color.WHITE);
        btn.setFont(StyleConfig.FONT_SIDEBAR);
    }
}