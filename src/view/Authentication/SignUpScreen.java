package view.Authentication;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class SignUpScreen extends JFrame {

    private final Color MAIN_BG_COLOR = new Color(0xE4EFE7);
    private final Color CARD_BG_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(0x2D3E35);
    private final Color ACCENT_COLOR = new Color(0x4A7C59);
    private final Color FIELD_BG = new Color(0xF5F9F6);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JPasswordField txtSecretKey;
    private JTextField txtStudentId;
    private JTextField txtFullName;
    private JTextField txtCourse; 
    private JTextField txtYearLevel; 
    
    private JButton btnSignUp;
    private JButton btnCancel;

    public SignUpScreen() {
        setTitle("Create Account");
        setSize(900, 800); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(MAIN_BG_COLOR);
        add(mainPanel);

        JPanel cardPanel = new RoundedPanel(30, CARD_BG_COLOR);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(40, 50, 40, 50)); 

        JPanel cardContainer = new JPanel(new BorderLayout());
        cardContainer.setOpaque(false);
        cardContainer.add(cardPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 50; 
        gbc.ipady = 20; 
        mainPanel.add(cardContainer, gbc);

        JLabel lblTitle = new JLabel("Sign Up");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Join us today");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = createStyledTextField("Username");
        txtPassword = createStyledPasswordField("Password");
        txtConfirmPassword = createStyledPasswordField("Confirm Password");
        txtStudentId = createStyledTextField("Student ID (e.g., S-2024-001)");
        txtFullName = createStyledTextField("Full Name");
        txtCourse = createStyledTextField("Course (e.g., BSCS)");
        txtYearLevel = createStyledTextField("Year Level (e.g., 1)");
        txtSecretKey = createStyledPasswordField("Admin Key (Optional)");

        btnSignUp = createStyledButton("Create Account", ACCENT_COLOR, Color.WHITE);
        btnCancel = createStyledButton("Cancel", Color.WHITE, ACCENT_COLOR);
        btnCancel.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));

        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(lblSubtitle);
        cardPanel.add(Box.createVerticalStrut(20));

        cardPanel.add(new JLabel("Username"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createVerticalStrut(10));

        cardPanel.add(new JLabel("Password"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createVerticalStrut(10));

        cardPanel.add(new JLabel("Confirm Password"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtConfirmPassword);
        cardPanel.add(Box.createVerticalStrut(10));
        
        cardPanel.add(new JLabel("Full Name"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtFullName);
        cardPanel.add(Box.createVerticalStrut(10));
        
        cardPanel.add(new JLabel("Student ID"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtStudentId);
        cardPanel.add(Box.createVerticalStrut(10));
        
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JPanel p1 = new JPanel(); p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS)); p1.setOpaque(false);
        p1.add(new JLabel("Course"));
        p1.add(Box.createVerticalStrut(5));
        p1.add(txtCourse);
        
        JPanel p2 = new JPanel(); p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS)); p2.setOpaque(false);
        p2.add(new JLabel("Year Level"));
        p2.add(Box.createVerticalStrut(5));
        p2.add(txtYearLevel);
        
        row.add(p1);
        row.add(p2);
        cardPanel.add(row);
        cardPanel.add(Box.createVerticalStrut(10));
         
        cardPanel.add(new JLabel("Secret Key (For Admins Only)"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtSecretKey);
        cardPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(500, 45));

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSignUp);

        cardPanel.add(buttonPanel);
        
        // --- FIX: Add Enter Key Navigation ---
        // Chain focus traversal
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> txtConfirmPassword.requestFocus());
        txtConfirmPassword.addActionListener(e -> txtFullName.requestFocus());
        txtFullName.addActionListener(e -> txtStudentId.requestFocus());
        txtStudentId.addActionListener(e -> txtCourse.requestFocus());
        txtCourse.addActionListener(e -> txtYearLevel.requestFocus());
        txtYearLevel.addActionListener(e -> txtSecretKey.requestFocus());
        
        // On last field, trigger sign up
        txtSecretKey.addActionListener(e -> btnSignUp.doClick());

        setVisible(true);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        styleBasicInput(field);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        styleBasicInput(field);
        return field;
    }

    private void styleBasicInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(ACCENT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xE0E0E0)), 
                BorderFactory.createEmptyBorder(10, 10, 10, 10) 
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBackground(Color.WHITE);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR), 
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBackground(FIELD_BG);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xE0E0E0)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if(bg != Color.WHITE) btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public JButton getSignUpButton() { return btnSignUp; }
    public JButton getCancelButton() { return btnCancel; }
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getConfirmPassword() { return new String(txtConfirmPassword.getPassword()); }
    public String getSecretKey() { return new String(txtSecretKey.getPassword()).trim(); }
    public String getStudentId() { return txtStudentId.getText().trim(); }
    public String getFullName() { return txtFullName.getText().trim(); }
    public String getCourse() { return txtCourse.getText().trim(); }
    public String getYearLevel() { return txtYearLevel.getText().trim(); }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        }
    }
}