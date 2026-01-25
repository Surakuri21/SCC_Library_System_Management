package view.Authentication;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginScreen extends JFrame {

    // --- COLOR PALETTE ---
    private final Color MAIN_BG_COLOR = new Color(0xE4EFE7);
    private final Color CARD_BG_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(0x2D3E35);
    private final Color ACCENT_COLOR = new Color(0x4A7C59);
    private final Color FIELD_BG = new Color(0xF5F9F6);

    // --- COMPONENTS ---
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginScreen() {
        // Frame Setup
        setTitle("Library System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600); // Matches SignUpView size for consistency
        setLocationRelativeTo(null);

        // 1. Main Background
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(MAIN_BG_COLOR);
        add(mainPanel);

        // 2. The Floating Card
        JPanel cardPanel = new RoundedPanel(30, CARD_BG_COLOR);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Container to center the card without stretching it
        JPanel cardContainer = new JPanel(new BorderLayout());
        cardContainer.setOpaque(false);
        cardContainer.add(cardPanel, BorderLayout.CENTER);

        mainPanel.add(cardContainer);

        // --- UI ELEMENTS ---

        // Header
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Inputs
        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();

        // Buttons
        loginButton = createStyledButton("Login", ACCENT_COLOR, Color.WHITE);

        // Secondary Action (Sign Up) styled as a "Ghost Button"
        signUpButton = createStyledButton("Sign Up (Student)", Color.WHITE, ACCENT_COLOR);
        signUpButton.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));

        // --- LAYOUT ASSEMBLY ---

        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalStrut(35));

        // Username Section
        cardPanel.add(new JLabel("Username"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(usernameField);
        cardPanel.add(Box.createVerticalStrut(20));

        // Password Section
        cardPanel.add(new JLabel("Password"));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(40));

        // Button Section
        // Login Button (Full Width)
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cardPanel.add(loginButton);

        cardPanel.add(Box.createVerticalStrut(15));

        // Sign Up Section (Centered)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel noAccountLabel = new JLabel("No account? ");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAccountLabel.setForeground(Color.GRAY);

        // Adjust sign up button size for the footer
        signUpButton.setPreferredSize(new Dimension(140, 30));
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        footerPanel.add(noAccountLabel);
        footerPanel.add(signUpButton);

        cardPanel.add(footerPanel);

        setVisible(true);
    }

    // --- STYLING HELPERS ---

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        styleBasicInput(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
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

        // Focus Animation Effect
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
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover Effect
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

    // --- GETTERS ---
    // These match the original code so that Controller works perfectly
    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public JButton getLoginButton() { return loginButton; }
    public JButton getSignUpButton() { return signUpButton; }
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }

    // --- CUSTOM ROUNDED PANEL ---
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