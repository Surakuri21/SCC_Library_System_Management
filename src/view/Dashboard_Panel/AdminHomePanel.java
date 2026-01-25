package view.Dashboard_Panel;

import database.BookDAO;
import database.StudentDAO;
import database.TransactionDAO;
import model.Books;
import model.Student;
import model.Transaction;
import model.User;
import view.Dialogs.BorrowedBooksDialog;
import view.Dialogs.ItemSelectionDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminHomePanel extends JPanel {

    // --- THEME COLORS (Copied from BookManagementPanel) ---
    private static final Color BG_CREAM = new Color(0xEB, 0xF4, 0xDD);
    private static final Color SAGE_GREEN = new Color(0x90, 0xAB, 0x8B);
    private static final Color FOREST_GREEN = new Color(0x5A, 0x78, 0x63);
    private static final Color DARK_SLATE = new Color(0x3B, 0x49, 0x53);
    private static final Color PURE_WHITE = Color.WHITE;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    private DefaultTableModel tableModel;
    private TransactionDAO transactionDAO;
    private BookDAO bookDAO;
    private StudentDAO studentDAO;

    // Labels for dynamic stats
    private JLabel lblTotalBooksVal, lblActiveMembersVal, lblBorrowedVal, lblOverdueVal;

    public AdminHomePanel(User user) {
        this.transactionDAO = new TransactionDAO();
        this.bookDAO = new BookDAO();
        this.studentDAO = new StudentDAO();

        setLayout(new BorderLayout(0, 25));
        setBackground(BG_CREAM); // Applied Theme Color
        setBorder(new EmptyBorder(30, 40, 30, 40)); // Applied Theme Padding

        // --- 1. HEADER SECTION ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(TITLE_FONT); // Applied Theme Font
        title.setForeground(DARK_SLATE); // Applied Theme Color

        // Dynamic Date
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        JLabel subTitle = new JLabel(dateStr);
        subTitle.setFont(MAIN_FONT); // Applied Theme Font
        subTitle.setForeground(DARK_SLATE); // Applied Theme Color

        headerPanel.add(title);
        headerPanel.add(subTitle);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. STATS CARDS (The 4 Colored Boxes) ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 140));

        // Initialize Stat Labels
        lblTotalBooksVal = new JLabel("0");
        lblActiveMembersVal = new JLabel("0");
        lblBorrowedVal = new JLabel("0");
        lblOverdueVal = new JLabel("0");

        // Add Cards with different colors
        
        // 1. Total Books (Light Blue)
        JPanel totalBooksCard = createStatCard("Total Books", lblTotalBooksVal, "+12%", new Color(220, 240, 255));
        totalBooksCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        totalBooksCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<Books> allBooks = bookDAO.getAllBooks();
                ItemSelectionDialog<Books> dialog = new ItemSelectionDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(AdminHomePanel.this),
                    "All Books",
                    allBooks,
                    new String[]{"ID", "Title", "Author", "Qty"},
                    b -> new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getQuantity()},
                    b -> b.getId() + " " + b.getTitle() + " " + b.getAuthor(), 
                    Books::getId,
                    Books::getTitle
                );
                dialog.setVisible(true);
            }
        });
        statsPanel.add(totalBooksCard); 
        
        // 2. Active Members (Light Green)
        JPanel activeMembersCard = createStatCard("Active Members", lblActiveMembersVal, "+8%", new Color(220, 245, 230));
        activeMembersCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        activeMembersCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<Student> allStudents = studentDAO.getAllStudents();
                ItemSelectionDialog<Student> dialog = new ItemSelectionDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(AdminHomePanel.this),
                    "Active Members",
                    allStudents,
                    new String[]{"ID", "Student ID", "Name"},
                    s -> new Object[]{s.getId(), s.getStudentId(), s.getName()},
                    s -> s.getId() + " " + s.getName() + " " + s.getStudentId(), 
                    Student::getId,
                    Student::getName
                );
                dialog.setVisible(true);
            }
        });
        statsPanel.add(activeMembersCard); 
        
        // 3. Books Borrowed (Beige)
        JPanel borrowedCard = createStatCard("Books Borrowed", lblBorrowedVal, "-3%", new Color(245, 240, 220));
        borrowedCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        borrowedCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new BorrowedBooksDialog((JFrame) SwingUtilities.getWindowAncestor(AdminHomePanel.this), user, "My Borrowed Books").setVisible(true);
            }
        });
        statsPanel.add(borrowedCard);
        
        // 4. Overdue Books (Light Red)
        JPanel overdueCard = createStatCard("Overdue Books", lblOverdueVal, "0%", new Color(255, 230, 230));
        overdueCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        overdueCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new BorrowedBooksDialog((JFrame) SwingUtilities.getWindowAncestor(AdminHomePanel.this), user, "Overdue Books").setVisible(true);
            }
        });
        statsPanel.add(overdueCard);

        // --- 3. CENTER CONTENT (Recent Transactions Table) ---
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(statsPanel, BorderLayout.NORTH);

        // Create the Table Panel
        JPanel tablePanel = createTablePanel();

        // Add some spacing between stats and table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(30, 0, 0, 0));
        tableContainer.add(tablePanel, BorderLayout.CENTER);

        centerContainer.add(tableContainer, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // --- AUTO REFRESH LOGIC ---
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshTransactions();
                refreshStats(); 
            }
            @Override
            public void ancestorRemoved(AncestorEvent event) {}
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });

        // Initial Load
        refreshTransactions();
        refreshStats();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String percent, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(DARK_SLATE); // Applied Theme Color

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(MAIN_FONT); // Applied Theme Font
        lblTitle.setForeground(new Color(100, 100, 100));

        JLabel lblPercent = new JLabel(percent);
        lblPercent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPercent.setForeground(new Color(80, 80, 80));
        lblPercent.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        card.add(lblPercent, BorderLayout.NORTH);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PURE_WHITE); // Applied Theme Color
        // Applied Theme Border
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SAGE_GREEN, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Table Header
        JLabel lblTitle = new JLabel("Recent Transactions");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(DARK_SLATE); // Applied Theme Color
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Student Name", "Book Title", "Date Issued", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        styleTable(table); // Applied Theme Table Styling

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setForeground(DARK_SLATE);
        table.setRowHeight(45); 
        table.setShowVerticalLines(false); 
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(BG_CREAM); 
        table.setSelectionForeground(DARK_SLATE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PURE_WHITE);
        header.setForeground(SAGE_GREEN); 
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, SAGE_GREEN));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void refreshTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> recent = transactionDAO.getRecentTransactions(10);

        if(recent != null) {
            for (Transaction t : recent) {
                tableModel.addRow(new Object[]{
                        t.getStudentName(),
                        t.getBookTitle(),
                        t.getIssueDate(),
                        t.getStatus()
                });
            }
        }
    }
    
    private void refreshStats() {
        try {
            int totalBooks = bookDAO.getTotalBooksCount();
            lblTotalBooksVal.setText(String.valueOf(totalBooks));
            
            int totalStudents = studentDAO.getTotalStudentsCount();
            lblActiveMembersVal.setText(String.valueOf(totalStudents));
            
            int borrowedCount = transactionDAO.getTotalBorrowedCount(); 
            lblBorrowedVal.setText(String.valueOf(borrowedCount));
            
            int overdueCount = transactionDAO.getOverdueCount(); 
            lblOverdueVal.setText(String.valueOf(overdueCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}