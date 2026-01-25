package view.Dashboard_Panel;

import database.AnnouncementDAO;
import database.TransactionDAO;
import database.UserDAO;
import model.Announcement;
import model.User;
import view.Dialogs.BorrowedBooksDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StudentHomePanel extends JPanel {

    // --- THEME COLORS (Copied from BookManagementPanel) ---
    private static final Color BG_CREAM = new Color(0xEB, 0xF4, 0xDD);
    private static final Color SAGE_GREEN = new Color(0x90, 0xAB, 0x8B);
    private static final Color FOREST_GREEN = new Color(0x5A, 0x78, 0x63);
    private static final Color DARK_SLATE = new Color(0x3B, 0x49, 0x53);
    private static final Color PURE_WHITE = Color.WHITE;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    private User currentUser;
    private AnnouncementDAO announcementDAO;
    private TransactionDAO transactionDAO;
    private UserDAO userDAO;

    // UI Labels to refresh
    private JLabel lblBorrowedValue;
    private JLabel lblOverdueValue;
    private JPanel announcementContainer;

    public StudentHomePanel(User user) {
        this.currentUser = user;
        this.announcementDAO = new AnnouncementDAO();
        this.transactionDAO = new TransactionDAO();
        this.userDAO = new UserDAO();

        // 1. MAIN LAYOUT
        setLayout(new BorderLayout(0, 20)); // Vertical gap
        setBackground(BG_CREAM); // Applied Theme Color
        setBorder(new EmptyBorder(30, 40, 30, 40)); // Applied Theme Padding

        // 2. HEADER SECTION
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Welcome back, " + capitalize(user.getUsername()));
        title.setFont(TITLE_FONT); // Applied Theme Font
        title.setForeground(DARK_SLATE); // Applied Theme Color

        JLabel subtitle = new JLabel("Student Portal Dashboard");
        subtitle.setFont(MAIN_FONT); // Applied Theme Font
        subtitle.setForeground(new Color(100, 100, 100));

        headerPanel.add(title);
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // 3. STATS CARDS (Borrowed & Overdue) - Now at the top of content
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 Row, 2 Cols
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 140));

        // Create Labels
        lblBorrowedValue = new JLabel("0");
        lblOverdueValue = new JLabel("0");

        // Borrowed Card (Green Theme)
        JPanel borrowedCard = createStatCard("Borrowed Books", lblBorrowedValue, "View Details", new Color(220, 245, 230));
        borrowedCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        borrowedCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open Dialog logic
                new BorrowedBooksDialog((JFrame) SwingUtilities.getWindowAncestor(StudentHomePanel.this), currentUser, "My Borrowed Books").setVisible(true);
                refreshData(); // Refresh after dialog closes (in case Sync was used)
            }
        });

        // Overdue Card (Red Theme)
        JPanel overdueCard = createStatCard("Overdue Books", lblOverdueValue, "Action Required", new Color(255, 230, 230));
        overdueCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        overdueCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new BorrowedBooksDialog((JFrame) SwingUtilities.getWindowAncestor(StudentHomePanel.this), currentUser, "Overdue Books").setVisible(true);
            }
        });

        statsPanel.add(borrowedCard); 
        statsPanel.add(overdueCard);  

        // 4. ANNOUNCEMENTS SECTION (Now below stats)
        JPanel announceWrapper = new JPanel(new BorderLayout());
        announceWrapper.setOpaque(false);

        JLabel lblAnnounce = new JLabel("Recent Announcements");
        lblAnnounce.setFont(SUBHEADER_FONT); // Applied Theme Font
        lblAnnounce.setForeground(DARK_SLATE); // Applied Theme Color
        lblAnnounce.setBorder(new EmptyBorder(20, 0, 10, 0)); // Added top padding for separation

        announceWrapper.add(lblAnnounce, BorderLayout.NORTH);

        // Scrollable List of Announcements
        announcementContainer = new JPanel();
        announcementContainer.setLayout(new BoxLayout(announcementContainer, BoxLayout.Y_AXIS));
        announcementContainer.setBackground(PURE_WHITE);

        JScrollPane scrollPane = new JScrollPane(announcementContainer);
        // Applied Theme Border
        scrollPane.setBorder(new LineBorder(SAGE_GREEN, 1, true)); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        announceWrapper.add(scrollPane, BorderLayout.CENTER);

        // Wrapper for center content
        JPanel centerContent = new JPanel(new BorderLayout(0, 20));
        centerContent.setOpaque(false);
        
        // Add Stats at TOP of center
        centerContent.add(statsPanel, BorderLayout.NORTH);
        
        // Add Announcements at CENTER (filling remaining space)
        centerContent.add(announceWrapper, BorderLayout.CENTER);

        add(centerContent, BorderLayout.CENTER);

        // --- AUTO REFRESH LOGIC ---
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshData();
            }
            @Override
            public void ancestorRemoved(AncestorEvent event) {}
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });

        // Initial Load
        refreshData();
    }

    // --- FACTORY METHODS ---

    private JPanel createStatCard(String title, JLabel valueLabel, String subText, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(DARK_SLATE); // Applied Theme Color

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(MAIN_FONT); // Applied Theme Font
        lblTitle.setForeground(new Color(80, 80, 80));

        JLabel lblSub = new JLabel(subText + " →");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(new Color(100, 100, 100));
        lblSub.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createAnnouncementItem(Announcement a) {
        JPanel item = new JPanel(new BorderLayout(5, 5));
        item.setBackground(PURE_WHITE);
        item.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(a.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(DARK_SLATE); // Applied Theme Color

        JTextArea content = new JTextArea(a.getContent());
        content.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.setForeground(new Color(108, 117, 125)); // Muted Grey for body
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setOpaque(false);
        content.setEditable(false);

        JLabel date = new JLabel(a.getCreatedAt().toString().substring(0, 10));
        date.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        date.setForeground(new Color(150, 150, 150));

        item.add(title, BorderLayout.NORTH);
        item.add(content, BorderLayout.CENTER);
        item.add(date, BorderLayout.SOUTH);

        return item;
    }

    private void refreshData() {
        // 1. Update Stats
        try {
            int studentId = userDAO.getStudentByUserId(currentUser.getId()).getId();
            int borrowedCount = transactionDAO.getBorrowedCount(studentId);
            
            if (borrowedCount < 0) borrowedCount = 0;

            lblBorrowedValue.setText(String.valueOf(borrowedCount));
            
            // int overdueCount = transactionDAO.getOverdueCount(studentId); // Implement if available in DAO
            // lblOverdueValue.setText(String.valueOf(overdueCount));
        } catch (Exception e) {
            System.err.println("Could not load stats: " + e.getMessage());
        }

        // 2. Update Announcements
        announcementContainer.removeAll();
        List<Announcement> list = announcementDAO.getLatestAnnouncements();

        if (list.isEmpty()) {
            JLabel empty = new JLabel("No new announcements.");
            empty.setBorder(new EmptyBorder(20, 20, 20, 20));
            empty.setForeground(Color.GRAY);
            announcementContainer.add(empty);
        } else {
            for (Announcement a : list) {
                announcementContainer.add(createAnnouncementItem(a));
            }
        }
        announcementContainer.revalidate();
        announcementContainer.repaint();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}