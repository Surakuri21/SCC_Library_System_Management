package view.Dashboard_Panel;

import database.StudentDAO;
import model.Student;
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
import java.util.stream.Collectors;

public class StudentRecordsPanel extends JPanel {

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
    private StudentDAO studentDAO;
    private JTextField searchField;
    private List<Student> allStudents; // Cache for filtering

    public StudentRecordsPanel() {
        this.studentDAO = new StudentDAO();
        
        // 1. MAIN PANEL SETUP
        setLayout(new BorderLayout(20, 20)); 
        setBackground(BG_CREAM); 
        setBorder(new EmptyBorder(30, 40, 30, 40)); 

        // 2. HEADER SECTION
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Student Records");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DARK_SLATE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Controls Panel (Search + Delete)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);
        
        // Search Bar
        searchField = createStyledSearchField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterStudents(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterStudents(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterStudents(searchField.getText()); }
        });
        controlsPanel.add(searchField);
        
        JButton btnDelete = createSecondaryButton("Delete Student");
        btnDelete.addActionListener(e -> handleDelete());
        controlsPanel.add(btnDelete);
        
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // 3. TABLE SECTION
        String[] columns = {"ID", "Student ID", "Name", "Course", "Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        table = new JTable(tableModel);
        styleTable(table);
        
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

        // 4. BOTTOM PANEL (Removed Refresh Button)
        // JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // bottomPanel.setOpaque(false);
        // add(bottomPanel, BorderLayout.SOUTH);

        loadStudents();
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
        // Placeholder text logic could be added here if needed, but simple is fine
        return field;
    }
    
    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(FOREST_GREEN);
        btn.setBackground(PURE_WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(FOREST_GREEN, 1));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadStudents() {
        allStudents = studentDAO.getAllStudents();
        filterStudents(searchField.getText());
    }
    
    private void filterStudents(String query) {
        tableModel.setRowCount(0);
        if (allStudents == null) return;
        
        String q = query.toLowerCase();
        List<Student> filtered = allStudents.stream()
            .filter(s -> s.getName().toLowerCase().contains(q) || 
                         s.getStudentId().toLowerCase().contains(q) ||
                         s.getCourse().toLowerCase().contains(q))
            .collect(Collectors.toList());
            
        for (Student s : filtered) {
            tableModel.addRow(new Object[]{
                s.getId(),
                s.getStudentId(),
                s.getName(),
                s.getCourse(),
                s.getYearLevel()
            });
        }
    }

    private void handleDelete() {
        // Use current filtered list for selection dialog if possible, or reload all
        // For consistency, let's reload all to ensure we don't miss anyone not in current view
        List<Student> currentList = studentDAO.getAllStudents();
        
        ItemSelectionDialog<Student> picker = new ItemSelectionDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Select Student to Delete",
            currentList,
            new String[]{"ID", "Student ID", "Name"},
            s -> new Object[]{s.getId(), s.getStudentId(), s.getName()},
            s -> s.getId() + " " + s.getName() + " " + s.getStudentId(), 
            Student::getId,
            Student::getName
        );
        
        picker.setVisible(true);
        
        if (picker.getSelectedId() != -1) {
            int studentId = picker.getSelectedId();
            Student studentToDelete = studentDAO.getStudentById(studentId);
            
            if (studentToDelete != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete this student?\n\n" +
                    "Name: " + studentToDelete.getName() + "\n" +
                    "ID: " + studentToDelete.getStudentId() + "\n\n" +
                    "This action cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    studentDAO.deleteStudent(studentId);
                    loadStudents(); // Automatic Refresh
                    JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                }
            }
        }
    }
}