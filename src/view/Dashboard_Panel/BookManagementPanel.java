package view.Dashboard_Panel;

import database.BookDAO;
import model.Books;
import model.User;
import view.Dialogs.BookDetailsDialog;
import view.Dialogs.BookEditorDialog;
import view.Dialogs.DeleteConfirmationDialog;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BookManagementPanel extends JPanel {

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
    private BookDAO bookDAO;
    private User currentUser;
    private JTextField searchField;

    public BookManagementPanel(User user) {
        this.currentUser = user;
        this.bookDAO = new BookDAO();

        // 1. MAIN PANEL SETUP
        setLayout(new BorderLayout(20, 20)); 
        setBackground(BG_CREAM); 
        setBorder(new EmptyBorder(30, 40, 30, 40)); 

        // 2. HEADER SECTION
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Library Books");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DARK_SLATE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);

        searchField = createStyledSearchField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadBooks(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { loadBooks(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { loadBooks(searchField.getText()); }
        });
        controlsPanel.add(searchField);

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            JButton btnAdd = createPrimaryButton("+ Add Book");
            JButton btnUpdate = createSecondaryButton("Update");
            JButton btnDelete = createSecondaryButton("Delete");
            
            btnAdd.addActionListener(e -> {
                BookEditorDialog dialog = new BookEditorDialog((JFrame) SwingUtilities.getWindowAncestor(this));
                dialog.setVisible(true);
                if (dialog.isBookAdded()) {
                    loadBooks("");
                }
            });

            btnUpdate.addActionListener(e -> {
                // Open ItemSelectionDialog to select a book to update
                List<Books> allBooks = bookDAO.getAllBooks();
                
                ItemSelectionDialog<Books> picker = new ItemSelectionDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Select Book to Update",
                    allBooks,
                    new String[]{"ID", "Title", "Author", "Qty"},
                    b -> new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getQuantity()},
                    b -> b.getId() + " " + b.getTitle() + " " + b.getAuthor(), 
                    Books::getId,
                    Books::getTitle
                );
                
                picker.setVisible(true);
                
                if (picker.getSelectedId() != -1) {
                    int bookId = picker.getSelectedId();
                    Books bookToUpdate = bookDAO.getBookById(bookId);
                    
                    if (bookToUpdate != null) {
                        BookEditorDialog dialog = new BookEditorDialog((JFrame) SwingUtilities.getWindowAncestor(this), bookToUpdate);
                        dialog.setVisible(true);
                        if (dialog.isBookAdded()) { // Reusing this flag for update success
                            loadBooks("");
                        }
                    }
                }
            });

            btnDelete.addActionListener(e -> {
                // Open ItemSelectionDialog to select a book to delete
                List<Books> allBooks = bookDAO.getAllBooks();
                
                ItemSelectionDialog<Books> picker = new ItemSelectionDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Select Book to Delete",
                    allBooks,
                    new String[]{"ID", "Title", "Author", "Qty"},
                    b -> new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getQuantity()},
                    b -> b.getId() + " " + b.getTitle() + " " + b.getAuthor(), 
                    Books::getId,
                    Books::getTitle
                );
                
                picker.setVisible(true);
                
                if (picker.getSelectedId() != -1) {
                    int bookId = picker.getSelectedId();
                    Books bookToDelete = bookDAO.getBookById(bookId);
                    
                    if (bookToDelete != null) {
                        List<Books> booksToDelete = new ArrayList<>();
                        booksToDelete.add(bookToDelete);
                        
                        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog((JFrame) SwingUtilities.getWindowAncestor(this), booksToDelete);
                        dialog.setVisible(true);

                        if (dialog.isConfirmed()) {
                            bookDAO.deleteBook(bookId);
                            loadBooks(""); // Refresh table
                            JOptionPane.showMessageDialog(this, "Book deleted successfully.");
                        }
                    }
                }
            });

            controlsPanel.add(btnDelete);
            controlsPanel.add(btnUpdate);
            controlsPanel.add(btnAdd);    
        } else {
            JButton btnRefresh = createSecondaryButton("Refresh");
            btnRefresh.addActionListener(e -> {
                searchField.setText("");
                loadBooks("");
            });
            controlsPanel.add(btnRefresh);
        }

        headerPanel.add(controlsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 3. TABLE SECTION
        String[] columns = {"ID", "Title", "Author", "Category", "ISBN", "Qty", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        table = new JTable(tableModel);
        styleTable(table); 

        // --- MOUSE LISTENER FOR CLICKING BOOKS ---
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single click
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int bookId = (int) tableModel.getValueAt(row, 0);
                        // Fetch full book object
                        Books book = bookDAO.getBookById(bookId);
                        if (book != null) {
                            // Open Details Dialog
                            new BookDetailsDialog((JFrame) SwingUtilities.getWindowAncestor(BookManagementPanel.this), currentUser, book).setVisible(true);
                        }
                    }
                }
            }
        });

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

        loadBooks("");
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
        btn.setPreferredSize(new Dimension(120, 40));
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
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadBooks(String keyword) {
        tableModel.setRowCount(0); 
        List<Books> books = bookDAO.searchBooks(keyword);
        
        for (Books b : books) {
            tableModel.addRow(new Object[]{
                b.getId(),
                b.getTitle(),
                b.getAuthor(),
                b.getCategory(),
                b.getIsbn(),
                b.getQuantity(),
                b.getStatus()
            });
        }
    }
}