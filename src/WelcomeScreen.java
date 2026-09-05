import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
Main title screen featuring difficulty selection and progress status.
*/
public class WelcomeScreen extends JFrame implements ActionListener {

    private final RoundedButton startButton;
    private final RoundedButton exitButton;
    public WelcomeScreen() {
        setTitle("Tile Matching Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ModernUI.enableGraphicsAntialiasing(g2);
                GradientPaint bg = new GradientPaint(0, 0, ModernUI.BG_DARK_TOP, 0, getHeight(), ModernUI.BG_DARK_BOTTOM);
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        JPanel heroCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ModernUI.enableGraphicsAntialiasing(g2);
                g2.setColor(ModernUI.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2.setColor(ModernUI.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 32, 32);
                g2.dispose();
            }
        };
        heroCard.setOpaque(false);
        heroCard.setPreferredSize(new Dimension(560, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 20, 5, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel gameName = new JLabel("TILES MATCHING", SwingConstants.CENTER);
        gameName.setFont(ModernUI.FONT_TITLE);
        gameName.setForeground(ModernUI.TEXT_PRIMARY);
        heroCard.add(gameName, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 20, 15, 20);
        JLabel subtitle = new JLabel("PUZZLE ADVENTURE", SwingConstants.CENTER);
        subtitle.setFont(ModernUI.FONT_SUBTITLE);
        subtitle.setForeground(ModernUI.ACCENT_CYAN);
        heroCard.add(subtitle, gbc);

        // Start Button
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 12, 20);
        startButton = new RoundedButton("START GAME", ModernIcons.createPlayIcon(24, ModernUI.TEXT_PRIMARY));
        startButton.setPreferredSize(new Dimension(280, 56));
        startButton.setFont(ModernUI.FONT_BUTTON);
        startButton.setGradientEndColor(ModernUI.ACCENT_PURPLE_END);
        startButton.setCornerRadius(28);
        startButton.addActionListener(this);
        heroCard.add(startButton, gbc);

        // Exit Button
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 15, 20);
        exitButton = new RoundedButton("EXIT", ModernUI.BTN_SECONDARY_BASE, ModernUI.BTN_SECONDARY_HOVER,
                ModernUI.TEXT_SECONDARY, ModernIcons.createExitIcon(20, ModernUI.TEXT_SECONDARY));
        exitButton.setPreferredSize(new Dimension(280, 44));
        exitButton.setFont(ModernUI.FONT_BUTTON);
        exitButton.setCornerRadius(22);
        exitButton.addActionListener(this);
        heroCard.add(exitButton, gbc);

        mainPanel.add(heroCard);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            new LevelWindow();
            dispose();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}
