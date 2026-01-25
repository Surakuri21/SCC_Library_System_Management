package view.Dialogs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemSelectionDialog<T> extends JDialog {

    private int selectedId = -1;
    private String selectedName = "";
    private JTable table;
    private DefaultTableModel model;
    private List<T> allItems;
    private Function<T, Object[]> rowMapper;
    private Function<T, String> searchMapper;
    private Function<T, Integer> idMapper;
    private Function<T, String> nameMapper;

    public ItemSelectionDialog(JFrame parent, String title, List<T> items, String[] columns, 
                            Function<T, Object[]> rowMapper, 
                            Function<T, String> searchMapper,
                            Function<T, Integer> idMapper,
                            Function<T, String> nameMapper) {
        super(parent, title, true);
        this.allItems = items;
        this.rowMapper = rowMapper;
        this.searchMapper = searchMapper;
        this.idMapper = idMapper;
        this.nameMapper = nameMapper;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Search Bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(20);
        
        // REAL-TIME SEARCH LISTENER
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { filter(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { filter(txtSearch.getText()); }
        });
        
        // ENTER KEY LISTENER (Select first row if only one, or selected row)
        txtSearch.addActionListener(e -> {
            if (table.getRowCount() > 0) {
                if (table.getSelectedRow() == -1) {
                    table.setRowSelectionInterval(0, 0); // Select first row automatically
                }
                selectAndClose();
            }
        });
        
        // DOWN ARROW to move focus to table
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    table.requestFocus();
                    if (table.getRowCount() > 0) {
                        table.setRowSelectionInterval(0, 0);
                    }
                }
            }
        });
        
        topPanel.add(new JLabel("Search:"));
        topPanel.add(txtSearch);
        
        add(topPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Double Click to Select
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectAndClose();
                }
            }
        });
        
        // ENTER KEY on Table
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectAndClose();
                    e.consume(); // Prevent default Enter behavior (moving down)
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSelect = new JButton("Select");
        btnSelect.addActionListener(e -> selectAndClose());
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        
        bottomPanel.add(btnSelect);
        bottomPanel.add(btnCancel);
        add(bottomPanel, BorderLayout.SOUTH);

        filter(""); // Load all
    }

    private void filter(String query) {
        model.setRowCount(0);
        String q = query.toLowerCase();
        List<T> filtered = allItems.stream()
                .filter(item -> searchMapper.apply(item).toLowerCase().contains(q))
                .collect(Collectors.toList());
        
        for (T item : filtered) {
            model.addRow(rowMapper.apply(item));
        }
    }

    private void selectAndClose() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        // We need to find the original object. 
        // Since table might be filtered, we can't just use index.
        // But we can assume the ID is in column 0.
        Object val = table.getValueAt(row, 0);
        int id;
        if (val instanceof Integer) {
            id = (Integer) val;
        } else {
            // Try parsing if it's a string or something else
            try {
                id = Integer.parseInt(val.toString());
            } catch (NumberFormatException e) {
                // Should not happen if we put ID in col 0
                return;
            }
        }
        
        selectedId = id;
        
        // Find name for display
        for(T item : allItems) {
            if(idMapper.apply(item) == id) {
                selectedName = nameMapper.apply(item);
                break;
            }
        }
        
        dispose();
    }

    public int getSelectedId() { return selectedId; }
    public String getSelectedName() { return selectedName; }
}