package view.Style;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class StyleConfig {

    // --- COLOR PALETTE (From your Figma Screenshots) ---

    // Sidebar & Brand Colors
    // "Chalet Green" - Used for the Sidebar Background
    public static final Color COLOR_SIDEBAR_BG = new Color(70, 90, 50); // #465A32

    // "Pine Glade" - Used for Active Buttons (e.g., The selected Dashboard tab)
    public static final Color COLOR_ACCENT_BG = new Color(195, 198, 138); // #C3C68A

    // "Tara" - A lighter variation if needed for hover effects
    public static final Color COLOR_ACCENT_LIGHT = new Color(207, 241, 213); // #CFF1D5

    // Main Content Area
    // A clean off-white background for the main area
    public static final Color COLOR_MAIN_BG = new Color(248, 249, 250);
    public static final Color COLOR_WHITE = Color.WHITE;

    // Text Colors
    public static final Color COLOR_TEXT_SIDEBAR = Color.WHITE; // White text on dark green sidebar
    public static final Color COLOR_TEXT_HEADER = new Color(33, 37, 41); // Dark Grey for headers
    public static final Color COLOR_TEXT_BODY = new Color(108, 117, 125); // Muted Grey for labels

    // --- FONTS (Segoe UI for that clean Windows look) ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
   public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 18); // Added this line
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_SIDEBAR_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    // --- SPACING & BORDERS ---
    public static final Border PADDING_MAIN = BorderFactory.createEmptyBorder(30, 30, 30, 30);
    public static final Border PADDING_SIDEBAR_ITEM = BorderFactory.createEmptyBorder(12, 20, 12, 20);
}