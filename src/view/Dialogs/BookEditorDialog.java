package view.Dialogs;

import database.BookDAO;
import model.Books;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class BookEditorDialog extends JDialog {

    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtCategory;
    private JTextField txtIsbn;
    private JSpinner spnQuantity;
    private boolean bookAdded = false;
    private Books bookToUpdate; // If null, we are in ADD mode. If set, we are in UPDATE mode.

    // Colors
    private static final Color BG_CREAM = new Color(0xEB, 0xF4, 0xDD);
    private static final Color FOREST_GREEN = new Color(0x5A, 0x78, 0x63);
    private static final Color PURE_WHITE = Color.WHITE;

    // Constructor for Adding a Book
    public BookEditorDialog(JFrame parent) {
        this(parent, null);
    }

    // Constructor for Updating a Book
    public BookEditorDialog(JFrame parent, Books book) {
        super(parent, book == null ? "Add New Book" : "Update Book", true);
        this.bookToUpdate = book;
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_CREAM);

        // Header
        JLabel lblHeader = new JLabel(book == null ? "Enter Book Details" : "Edit Book Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(FOREST_GREEN);
        lblHeader.setBorder(new EmptyBorder(15, 20, 10, 20));
        add(lblHeader, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PURE_WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        txtTitle = createStyledTextField();
        txtAuthor = createStyledTextField();
        txtCategory = createStyledTextField();
        txtIsbn = createStyledTextField();
        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spnQuantity.setPreferredSize(new Dimension(100, 30));

        // Pre-fill fields if updating
        if (book != null) {
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthor());
            txtCategory.setText(book.getCategory());
            txtIsbn.setText(book.getIsbn());
            spnQuantity.setValue(book.getQuantity());
        }

        addFormField(formPanel, gbc, 0, "Title:", txtTitle);
        addFormField(formPanel, gbc, 1, "Author:", txtAuthor);
        addFormField(formPanel, gbc, 2, "Category:", txtCategory);
        addFormField(formPanel, gbc, 3, "ISBN:", txtIsbn);
        addFormField(formPanel, gbc, 4, "Quantity:", spnQuantity);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_CREAM);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());
        
        JButton btnSave = new JButton(book == null ? "Save Book" : "Update Book");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(FOREST_GREEN);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveBook());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private void saveBook() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = txtCategory.getText().trim();
        String isbn = txtIsbn.getText().trim();
        int quantity = (int) spnQuantity.getValue();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BookDAO dao = new BookDAO();
        
        if (bookToUpdate == null) {
            // ADD NEW BOOK
            Books newBook = new Books(0, title, author, isbn, quantity, category);
            dao.addBook(newBook);
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        } else {
            // UPDATE EXISTING BOOK
            bookToUpdate.setTitle(title);
            bookToUpdate.setAuthor(author);
            bookToUpdate.setCategory(category);
            bookToUpdate.setIsbn(isbn);
            bookToUpdate.setQuantity(quantity);
            dao.updateBook(bookToUpdate);
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
        }
        
        bookAdded = true;
        dispose();
    }

    public boolean isBookAdded() {
        return bookAdded;
    }
}