import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A modern flat/gradient button with rounded pill corners, smooth hover effects,
 * vector icon support, and press feedback.
 */
public class RoundedButton extends JButton {

    private Color baseColor;
    private Color hoverColor;
    private Color gradientEndColor;
    private int cornerRadius = 24;
    private boolean hovered = false;
    private boolean pressed = false;

    public RoundedButton(String text) {
        this(text, ModernUI.ACCENT_PURPLE_START, ModernUI.ACCENT_HOVER_START, ModernUI.TEXT_PRIMARY);
    }

    public RoundedButton(String text, Color baseColor, Color hoverColor, Color textColor) {
        this(text, baseColor, hoverColor, textColor, null);
    }

    public RoundedButton(String text, Icon icon) {
        this(text, ModernUI.ACCENT_PURPLE_START, ModernUI.ACCENT_HOVER_START, ModernUI.TEXT_PRIMARY, icon);
    }

    public RoundedButton(Icon icon) {
        this("", ModernUI.BTN_SECONDARY_BASE, ModernUI.BTN_SECONDARY_HOVER, ModernUI.TEXT_PRIMARY, icon);
    }

    public RoundedButton(String text, Color baseColor, Color hoverColor, Color textColor, Icon icon) {
        super(text);
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;

        if (icon != null) {
            setIcon(icon);
            setIconTextGap(8);
        }

        setFont(ModernUI.FONT_BUTTON);
        setForeground(textColor);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    hovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    pressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    pressed = false;
                    repaint();
                }
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setGradientEndColor(Color endColor) {
        this.gradientEndColor = endColor;
        repaint();
    }

    public void setBaseColor(Color color) {
        this.baseColor = color;
        repaint();
    }

    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ModernUI.enableGraphicsAntialiasing(g2);

        int w = getWidth();
        int h = getHeight();

        if (!isEnabled()) {
            g2.setColor(new Color(30, 41, 59, 150));
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
            g2.setColor(ModernUI.CARD_BORDER);
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
            return;
        }

        Color currentColor = hovered ? hoverColor : baseColor;

        if (gradientEndColor != null) {
            GradientPaint gp = new GradientPaint(0, 0, currentColor, 0, h, gradientEndColor);
            g2.setPaint(gp);
        } else {
            g2.setColor(currentColor);
        }

        // Draw shadow glow if hovered
        if (hovered && !pressed) {
            g2.setColor(new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 60));
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
            g2.setColor(currentColor);
        }

        // Draw main body
        int offset = pressed ? 1 : 0;
        g2.fillRoundRect(offset, offset, w - (offset * 2), h - (offset * 2), cornerRadius, cornerRadius);

        // Draw subtle border outline
        g2.setColor(hovered ? ModernUI.ACCENT_CYAN : new Color(255, 255, 255, 30));
        g2.drawRoundRect(offset, offset, w - 1 - (offset * 2), h - 1 - (offset * 2), cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
