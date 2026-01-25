package view.Dashboard_Panel;

import database.BookDAO;
import database.StudentDAO;
import database.TransactionDAO;
import model.Books;
import model.Student;
import model.Transaction;
import view.Dialogs.ItemSelectionDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class IssueReturnPanel extends JPanel {

    // --- THEME COLORS ---
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
    private JTextField searchField;
    
    // Selection State
    private int selectedStudentId = -1;
    private int selectedBookId = -1;
    private JLabel lblSelectedStudent;
    private JLabel lblSelectedBook;

    public IssueReturnPanel() {
        this.transactionDAO = new TransactionDAO();
        
        // 1. MAIN PANEL SETUP
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- TOP PANEL: ISSUE FORM & SEARCH ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        
        // 1. Issue Form
        // Use GridBagLayout for stable positioning
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SAGE_GREEN), "Issue Book", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), DARK_SLATE));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Student Button
        JButton btnSelectStudent = createSecondaryButton("Select Student");
        btnSelectStudent.addActionListener(e -> openStudentPicker());
        gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(btnSelectStudent, gbc);
        
        // Student Label (Flexible)
        lblSelectedStudent = new JLabel("None Selected");
        lblSelectedStudent.setFont(MAIN_FONT);
        lblSelectedStudent.setForeground(DARK_SLATE);
        gbc.gridx = 1; gbc.weightx = 0.5;
        formPanel.add(lblSelectedStudent, gbc);
        
        // Book Button
        JButton btnSelectBook = createSecondaryButton("Select Book");
        btnSelectBook.addActionListener(e -> openBookPicker());
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(btnSelectBook, gbc);
        
        // Book Label (Flexible)
        lblSelectedBook = new JLabel("None Selected");
        lblSelectedBook.setFont(MAIN_FONT);
        lblSelectedBook.setForeground(DARK_SLATE);
        gbc.gridx = 3; gbc.weightx = 0.5;
        formPanel.add(lblSelectedBook, gbc);
        
        // Issue Button (Fixed at end)
        JButton btnIssue = createPrimaryButton("Issue Book");
        btnIssue.addActionListener(e -> handleIssue());
        gbc.gridx = 4; gbc.weightx = 0;
        formPanel.add(btnIssue, gbc);
        
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // 2. Search Bar (Bottom of Top Panel)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Search Transactions:");
        lblSearch.setFont(MAIN_FONT);
        lblSearch.setForeground(DARK_SLATE);
        searchPanel.add(lblSearch);
        
        searchField = createStyledSearchField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onSearch(); }
            public void removeUpdate(DocumentEvent e) { onSearch(); }
            public void changedUpdate(DocumentEvent e) { onSearch(); }
        });
        searchPanel.add(searchField);
        
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: TABLE ---
        String[] columns = {"Trans ID", "Student", "Book ID", "Book Title", "Issue Date", "Return Date", "Status"};
        
        // Override isCellEditable to make it read-only
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        
        // Extra safety: Disable editing via editor
        table.setDefaultEditor(Object.class, null);
        
        // Prevent column reordering (optional but good for read-only)
        table.getTableHeader().setReorderingAllowed(false);
        
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(PURE_WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SAGE_GREEN, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(PURE_WHITE);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // --- BOTTOM: RETURN BUTTONS ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        // REMOVED: Force Return All for Student button as requested
        
        JButton btnReturn = createPrimaryButton("Return Selected Book");
        btnReturn.setBackground(new Color(0xE67E22)); // Keep orange for return action
        btnReturn.addActionListener(e -> handleReturn());
        bottomPanel.add(btnReturn);
        
        add(bottomPanel, BorderLayout.SOUTH);

        loadTransactions("");
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

    private JTextField createStyledSearchField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(250, 40));
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SAGE_GREEN, 1, true), 
                new EmptyBorder(5, 10, 5, 10) 
        ));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(PURE_WHITE);
        btn.setBackground(FOREST_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(FOREST_GREEN);
        btn.setBackground(PURE_WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(FOREST_GREEN, 1));
        btn.setPreferredSize(new Dimension(160, 35)); // Slightly wider for "Return All"
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void onSearch() {
        // When searching, we should clear the specific student filter to allow global search
        if (selectedStudentId != -1 && !searchField.getText().trim().isEmpty()) {
            selectedStudentId = -1;
            lblSelectedStudent.setText("None Selected");
            lblSelectedStudent.setForeground(Color.GRAY);
        }
        loadTransactions(searchField.getText());
    }

    private void openStudentPicker() {
        StudentDAO dao = new StudentDAO();
        List<Student> students = dao.getAllStudents();
        
        ItemSelectionDialog<Student> dialog = new ItemSelectionDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Select Student",
            students,
            new String[]{"ID", "Student ID", "Name"},
            s -> new Object[]{s.getId(), s.getStudentId(), s.getName()},
            s -> s.getId() + " " + s.getName() + " " + s.getStudentId(), 
            Student::getId,
            Student::getName
        );
        
        dialog.setVisible(true);
        
        if (dialog.getSelectedId() != -1) {
            selectedStudentId = dialog.getSelectedId();
            lblSelectedStudent.setText(dialog.getSelectedName());
            lblSelectedStudent.setForeground(DARK_SLATE);
            
            // Clear search field when selecting a specific student
            searchField.setText("");
            
            // AUTO-FILTER TABLE FOR THIS STUDENT
            loadTransactionsForStudent(selectedStudentId);
        }
    }

    private void openBookPicker() {
        BookDAO dao = new BookDAO();
        List<Books> books = dao.getAllBooks();
        
        ItemSelectionDialog<Books> dialog = new ItemSelectionDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Select Book",
            books,
            new String[]{"ID", "Title", "Author", "Qty"},
            b -> new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getQuantity()},
            b -> b.getId() + " " + b.getTitle() + " " + b.getAuthor(), 
            Books::getId,
            Books::getTitle
        );
        
        dialog.setVisible(true);
        
        if (dialog.getSelectedId() != -1) {
            selectedBookId = dialog.getSelectedId();
            lblSelectedBook.setText(dialog.getSelectedName());
            lblSelectedBook.setForeground(DARK_SLATE);
        }
    }

    private void loadTransactions(String keyword) {
        tableModel.setRowCount(0);
        List<Transaction> list = transactionDAO.searchTransactions(keyword);
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getStudentName(),
                t.getBookId(),
                t.getBookTitle(),
                t.getIssueDate(),
                t.getReturnDate(),
                t.getStatus()
            });
        }
    }
    
    private void loadTransactionsForStudent(int studentId) {
        tableModel.setRowCount(0);
        List<Transaction> list = transactionDAO.getTransactionsByStudentId(studentId);
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getStudentName(),
                t.getBookId(),
                t.getBookTitle(),
                t.getIssueDate(),
                t.getReturnDate(),
                t.getStatus()
            });
        }
    }

    private void handleIssue() {
        if (selectedStudentId == -1 || selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select both a Student and a Book.");
            return;
        }
        
        // 1. Check Borrow Limit (Max 3 books)
        // REMOVED: Borrow limit check for Admin as requested.
        // Admins can override the limit.
        /*
        if (transactionDAO.hasReachedBorrowLimit(selectedStudentId)) {
            JOptionPane.showMessageDialog(this, "Student has reached the maximum borrow limit (3 books).", "Limit Reached", JOptionPane.WARNING_MESSAGE);
            return;
        }
        */
        
        // 2. Check for Reservation FIRST
        int reservationId = transactionDAO.getReservationId(selectedStudentId, selectedBookId);
        
        if (reservationId != -1) {
            // Scenario A: Reserved -> Auto-Issue
            transactionDAO.fulfillReservation(reservationId);
            JOptionPane.showMessageDialog(this, "Reservation Fulfilled! Book Issued.");
        } else {
            // NEW: Check if already borrowed to prevent duplicates
            if (transactionDAO.isBookAlreadyBorrowed(selectedStudentId, selectedBookId)) {
                JOptionPane.showMessageDialog(this, "This student has already borrowed this book.", "Duplicate Issue", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Scenario B: Not Reserved -> Ask Confirmation
            int confirm = JOptionPane.showConfirmDialog(this, 
                "This book was NOT reserved by this student.\n\n" +
                "Student: " + lblSelectedStudent.getText() + "\n" +
                "Book: " + lblSelectedBook.getText() + "\n\n" +
                "Are you sure you want to issue it?",
                "Confirm Issue",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                transactionDAO.issueBook(selectedStudentId, selectedBookId);
                JOptionPane.showMessageDialog(this, "Book Issued Successfully!");
            } else {
                return; // Cancelled
            }
        }
        
        // Reset Book selection
        selectedBookId = -1;
        lblSelectedBook.setText("None Selected");
        lblSelectedBook.setForeground(DARK_SLATE);
        
        // Reset Student selection to show ALL recent transactions
        selectedStudentId = -1;
        lblSelectedStudent.setText("None Selected");
        lblSelectedStudent.setForeground(DARK_SLATE);
        
        // Reload ALL transactions to show the most recent one at the top
        refresh();
        
        // Scroll to top to make sure the new transaction is visible
        if (table.getRowCount() > 0) {
            table.scrollRectToVisible(table.getCellRect(0, 0, true));
        }
    }

    private void handleReturn() {
        // 1. Open ItemSelectionDialog to select a book to return
        // We want to show a list of currently ISSUED books
        List<Transaction> issuedTransactions = transactionDAO.getIssuedBooksReport();
        
        if (issuedTransactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No books are currently issued.");
            return;
        }

        ItemSelectionDialog<Transaction> picker = new ItemSelectionDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Select Book to Return",
            issuedTransactions,
            new String[]{"Trans ID", "Student", "Book Title", "Issue Date"},
            t -> new Object[]{t.getId(), t.getStudentName(), t.getBookTitle(), t.getIssueDate()},
            t -> t.getId() + " " + t.getStudentName() + " " + t.getBookTitle(), 
            Transaction::getId,
            t -> t.getBookTitle() + " (by " + t.getStudentName() + ")"
        );
        
        picker.setVisible(true);
        
        if (picker.getSelectedId() != -1) {
            int transactionId = picker.getSelectedId();
            
            // Find the transaction object to get the book ID
            Transaction selectedTrans = null;
            for(Transaction t : issuedTransactions) {
                if(t.getId() == transactionId) {
                    selectedTrans = t;
                    break;
                }
            }
            
            if (selectedTrans != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Return Book: " + selectedTrans.getBookTitle() + "\n" +
                    "Student: " + selectedTrans.getStudentName() + "\n\n" +
                    "Confirm return?", 
                    "Confirm Return", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    transactionDAO.returnBook(transactionId, selectedTrans.getBookId());
                    refresh();
                    JOptionPane.showMessageDialog(this, "Book Returned Successfully!");
                }
            }
        }
    }
    
    // REMOVED: handleReturnAll method as it is no longer used
    // private void handleReturnAll() { ... }

    public void refresh() {
        // Clear table model first to ensure UI update
        tableModel.setRowCount(0);
        
        if (selectedStudentId != -1) {
            loadTransactionsForStudent(selectedStudentId);
        } else {
            // If search field is null (shouldn't happen but safe check), use empty string
            String keyword = (searchField != null) ? searchField.getText() : "";
            loadTransactions(keyword);
        }
        
        // Force UI repaint
        table.revalidate();
        table.repaint();
    }
}