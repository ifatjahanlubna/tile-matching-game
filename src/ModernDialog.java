import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * A themed modal replacement for {@code JOptionPane} confirmation dialogs.
 * Uses a dark glassmorphic card backdrop, anti-aliased typography, and
 * sleek rounded pill action buttons.
 */
public final class ModernDialog {

    private ModernDialog() {}

    /**
     * Shows a modal confirmation card over {@code owner} and blocks until
     * the player clicks one of the buttons.
     */
    public static int showOptions(JFrame owner, String title, String message,
                                   String[] optionLabels, int primaryIndex) {
        Overlay overlay = new Overlay(owner, title, message, optionLabels, primaryIndex);
        overlay.setVisible(true); // modal — blocks here until a button click disposes it
        return overlay.selected;
    }

    private static final class Overlay extends JDialog {
        private int selected = -1;

        Overlay(JFrame owner, String title, String message, String[] optionLabels, int primaryIndex) {
            super(owner, ModalityType.APPLICATION_MODAL);
            setUndecorated(true);
            setResizable(false);

            boolean translucent = isTranslucencySupported();
            if (translucent) {
                setBackground(new Color(0, 0, 0, 0));
            }

            JPanel scrim = buildScrim(translucent);
            JPanel card = buildCard(title, message, optionLabels, primaryIndex);

            GridBagConstraints centered = new GridBagConstraints();
            scrim.add(card, centered);
            setContentPane(scrim);

            setSize(owner.getSize());
            setLocationRelativeTo(owner);
        }

        private JPanel buildScrim(boolean translucent) {
            Color scrimColor = translucent ? new Color(8, 12, 24, 180) : new Color(15, 23, 42);
            JPanel scrim = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(scrimColor);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            scrim.setOpaque(false);
            return scrim;
        }

        private JPanel buildCard(String title, String message, String[] optionLabels, int primaryIndex) {
            JPanel card = new JPanel(new BorderLayout(0, 20)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    ModernUI.enableGraphicsAntialiasing(g2);
                    g2.setColor(ModernUI.CARD_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    g2.setColor(ModernUI.CARD_BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                    g2.dispose();
                }
            };
            card.setOpaque(false);
            card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(ModernUI.FONT_SUBTITLE);
            titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
            card.add(titleLabel, BorderLayout.NORTH);

            JLabel messageLabel = new JLabel(
                    "<html><div style='text-align:center;'>" + message.replace("\n", "<br>") + "</div></html>",
                    SwingConstants.CENTER);
            messageLabel.setFont(ModernUI.FONT_BODY);
            messageLabel.setForeground(ModernUI.TEXT_SECONDARY);
            card.add(messageLabel, BorderLayout.CENTER);

            card.add(buildButtonRow(optionLabels, primaryIndex), BorderLayout.SOUTH);
            return card;
        }

        private JPanel buildButtonRow(String[] optionLabels, int primaryIndex) {
            JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
            buttonRow.setOpaque(false);

            JButton defaultButton = null;
            for (int i = 0; i < optionLabels.length; i++) {
                boolean primary = (i == primaryIndex);
                RoundedButton button;

                if (primary) {
                    button = new RoundedButton(optionLabels[i], ModernUI.ACCENT_PURPLE_START, ModernUI.ACCENT_HOVER_START, ModernUI.TEXT_PRIMARY);
                    button.setGradientEndColor(ModernUI.ACCENT_PURPLE_END);
                } else {
                    button = new RoundedButton(optionLabels[i], ModernUI.BTN_SECONDARY_BASE, ModernUI.BTN_SECONDARY_HOVER, ModernUI.TEXT_SECONDARY);
                }

                button.setCornerRadius(22);
                int index = i;
                button.addActionListener(e -> {
                    selected = index;
                    dispose();
                });

                if (primary) {
                    defaultButton = button;
                }
                buttonRow.add(button);
            }

            if (defaultButton != null) {
                getRootPane().setDefaultButton(defaultButton);
            }
            return buttonRow;
        }

        private static boolean isTranslucencySupported() {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            return device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT);
        }
    }
}
