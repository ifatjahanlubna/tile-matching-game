import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Modern design system for the Tile Matching Game.
 * Centralizes color palettes, typography, and rendering hints.
 */
public final class ModernUI {

    private ModernUI() {}

    // Dark slate background palette
    public static final Color BG_DARK_TOP = new Color(15, 23, 42);       // #0F172A
    public static final Color BG_DARK_BOTTOM = new Color(30, 41, 59);   // #1E293B

    // Card & Panel surface colors
    public static final Color CARD_BG = new Color(30, 41, 59, 230);      // Glassmorphic translucent card
    public static final Color CARD_BORDER = new Color(51, 65, 85);       // #334155
    public static final Color HUD_BG = new Color(15, 23, 42, 210);

    // Accent Colors & Gradients
    public static final Color ACCENT_PURPLE_START = new Color(99, 102, 241); // #6366F1
    public static final Color ACCENT_PURPLE_END = new Color(139, 92, 246);   // #8B5CF6
    public static final Color ACCENT_HOVER_START = new Color(129, 140, 248); // #818CF8
    public static final Color ACCENT_HOVER_END = new Color(167, 139, 250);  // #A78BFA

    public static final Color ACCENT_CYAN = new Color(56, 189, 248);        // #38BDF8 (Selection Glow)
    public static final Color ACCENT_GOLD = new Color(251, 191, 36);        // #FBBF24 (Score / Stars)
    public static final Color ACCENT_ROSE = new Color(244, 63, 94);         // #F43F5E (Timer alert / Danger)
    public static final Color ACCENT_EMERALD = new Color(52, 211, 153);     // #34D399 (Unlocked / Pass)

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);     // #F8FAFC
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);   // #94A3B8
    public static final Color TEXT_MUTED = new Color(100, 116, 139);       // #64748B

    // Buttons Base & Hover Colors
    public static final Color BTN_SECONDARY_BASE = new Color(51, 65, 85);
    public static final Color BTN_SECONDARY_HOVER = new Color(71, 85, 105);

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 48);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Enables anti-aliasing on the given Graphics2D instance for smooth rendering.
     */
    public static void enableGraphicsAntialiasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
