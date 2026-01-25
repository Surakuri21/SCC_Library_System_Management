package view.Dashboard_Panel;

import database.TransactionDAO;
import model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {

    // --- THEME COLORS (Copied from BookManagementPanel) ---
    private static final Color BG_CREAM = new Color(0xEB, 0xF4, 0xDD);
    private static final Color SAGE_GREEN = new Color(0x90, 0xAB, 0x8B);
    private static final Color FOREST_GREEN = new Color(0x5A, 0x78, 0x63);
    private static final Color DARK_SLATE = new Color(0x3B, 0x49, 0x53);
    private static final Color PURE_WHITE = Color.WHITE;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionDAO transactionDAO;
    private JComboBox<String> reportTypeCombo;

    public ReportsPanel() {
        this.transactionDAO = new TransactionDAO();

        // 1. MAIN LAYOUT
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_CREAM); // Applied Theme Color
        setBorder(new EmptyBorder(30, 40, 30, 40)); // Applied Theme Padding

        // 2. HEADER SECTION
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("System Reports");
        title.setFont(TITLE_FONT); // Applied Theme Font
        title.setForeground(DARK_SLATE); // Applied Theme Color

        JLabel subtitle = new JLabel("Generate and view transaction history");
        subtitle.setFont(MAIN_FONT); // Applied Theme Font
        subtitle.setForeground(new Color(100, 100, 100));

        titleBox.add(title);
        titleBox.add(subtitle);
        headerPanel.add(titleBox, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // 3. CONTENT SECTION (Filter Bar + Table)
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);

        // -- Filter Bar --
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBackground(PURE_WHITE);
        filterBar.setBorder(new LineBorder(SAGE_GREEN, 1, true)); // Applied Theme Border

        JLabel lblFilter = new JLabel("Report Type:");
        lblFilter.setFont(MAIN_FONT);
        lblFilter.setForeground(DARK_SLATE);

        // Added "Returned Books" to the combo box
        reportTypeCombo = new JComboBox<>(new String[]{"Issued Books", "Returned Books", "All Transactions"});
        reportTypeCombo.setFont(MAIN_FONT);
        reportTypeCombo.setBackground(PURE_WHITE);
        reportTypeCombo.setPreferredSize(new Dimension(200, 35));

        JButton btnGenerate = createPrimaryButton("Generate Report");
        btnGenerate.addActionListener(e -> generateReport());

        filterBar.add(lblFilter);
        filterBar.add(reportTypeCombo);
        filterBar.add(btnGenerate);

        contentPanel.add(filterBar, BorderLayout.NORTH);

        // -- Table Panel --
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Initial Load
        generateReport();
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PURE_WHITE);
        // Applied Theme Border
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SAGE_GREEN, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columns = {"Student Name", "Book Title", "Issue Date", "Return Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PURE_WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
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

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(PURE_WHITE);
        btn.setBackground(FOREST_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void generateReport() {
        tableModel.setRowCount(0);
        String type = (String) reportTypeCombo.getSelectedItem();
        List<Transaction> list;

        if ("Issued Books".equals(type)) {
            list = transactionDAO.getIssuedBooksReport();
        } else if ("Returned Books".equals(type)) {
            list = transactionDAO.getReturnedBooksReport();
        } else {
            list = transactionDAO.getAllTransactions();
        }

        if (list != null) {
            for (Transaction t : list) {
                tableModel.addRow(new Object[]{
                        t.getStudentName(),
                        t.getBookTitle(),
                        t.getIssueDate(),
                        t.getReturnDate(),
                        t.getStatus()
                });
            }
        }
    }
}