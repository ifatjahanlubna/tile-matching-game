import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Supplier;

/**
 * Level selection screen. Shows 15 levels across Easy, Normal, and Hard category tabs.
 */
public class LevelWindow extends JFrame implements ActionListener {

    private static final List<Supplier<AbstractLevel>> LEVEL_LAUNCHERS = List.of(
            Level1::new, Level2::new, Level3::new, Level4::new, Level5::new,
            Level6::new, Level7::new, Level8::new, Level9::new, Level10::new,
            Level11::new, Level12::new, Level13::new, Level14::new, Level15::new
    );

    private static final String[] LEVEL_NAMES = {
            "Score Rush", "Score Rush, tighter", "Collect Cherries", "Clear the Ice", "Score + Collect",
            "Multi-collect", "Break the Locks", "Speed Score", "Chain Reaction", "Obstacle + Score",
            "Ingredient Drop", "Speed Run", "Full Clear", "Triple Combo", "Final Boss"
    };

    private final RoundedButton[] levelButtons;
    private final RoundedButton backButton;
    private final RoundedButton easyTab;
    private final RoundedButton normalTab;
    private final RoundedButton hardTab;
    private final JLabel progressLabel;

    private int activeTier = 1; // 1 = Easy (L1-5), 2 = Normal (L6-10), 3 = Hard (L11-15)

    public LevelWindow() {
        setTitle("Tile Matching - Select Level (1-15)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
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

        // Header Panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(800, 180));

        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.gridx = 0;
        hgbc.gridy = 0;
        hgbc.insets = new Insets(15, 10, 2, 10);

        JLabel titleLabel = new JLabel("SELECT LEVEL", SwingConstants.CENTER);
        titleLabel.setFont(ModernUI.FONT_TITLE);
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        headerPanel.add(titleLabel, hgbc);

        hgbc.gridy++;
        hgbc.insets = new Insets(0, 10, 8, 10);
        progressLabel = new JLabel("", ModernIcons.createScoreIcon(18, ModernUI.ACCENT_GOLD), SwingConstants.CENTER);
        progressLabel.setFont(ModernUI.FONT_BODY);
        progressLabel.setForeground(ModernUI.TEXT_SECONDARY);
        headerPanel.add(progressLabel, hgbc);

        // Tier / Category Tabs
        hgbc.gridy++;
        hgbc.insets = new Insets(0, 10, 5, 10);
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        tabPanel.setOpaque(false);

        JLabel tabTitle = new JLabel("Category: ");
        tabTitle.setFont(ModernUI.FONT_BODY);
        tabTitle.setForeground(ModernUI.TEXT_SECONDARY);
        tabPanel.add(tabTitle);

        easyTab = new RoundedButton("EASY (L1-5)");
        normalTab = new RoundedButton("NORMAL (L6-10)");
        hardTab = new RoundedButton("HARD (L11-15)");

        for (RoundedButton b : new RoundedButton[]{easyTab, normalTab, hardTab}) {
            b.setPreferredSize(new Dimension(130, 36));
            b.setFont(ModernUI.FONT_SMALL);
            b.setCornerRadius(18);
            tabPanel.add(b);
        }

        easyTab.addActionListener(e -> selectTier(1));
        normalTab.addActionListener(e -> {
            if (GameProgress.isTierUnlocked(2)) {
                selectTier(2);
            }
        });
        hardTab.addActionListener(e -> {
            if (GameProgress.isTierUnlocked(3)) {
                selectTier(3);
            }
        });
        headerPanel.add(tabPanel, hgbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Level Grid Container (5 buttons visible per tier)
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);

        JPanel buttonGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonGrid.setOpaque(false);
        buttonGrid.setPreferredSize(new Dimension(680, 420));

        levelButtons = new RoundedButton[5];

        for (int i = 0; i < 5; i++) {
            RoundedButton button = new RoundedButton("");
            button.setPreferredSize(new Dimension(190, 135));
            button.setCornerRadius(24);
            button.addActionListener(this);

            buttonGrid.add(button);
            levelButtons[i] = button;
        }

        centerContainer.add(buttonGrid);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // Determine starting active tier
        int highestUnlocked = GameProgress.getHighestUnlockedLevel();
        if (highestUnlocked >= 11) activeTier = 3;
        else if (highestUnlocked >= 6) activeTier = 2;
        else activeTier = 1;

        refreshCategoryTabLocks();
        selectTier(activeTier);

        // Bottom Navigation Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        bottomBar.setOpaque(false);
        bottomBar.setPreferredSize(new Dimension(800, 80));

        backButton = new RoundedButton("BACK TO MAIN MENU", ModernUI.BTN_SECONDARY_BASE,
                ModernUI.BTN_SECONDARY_HOVER, ModernUI.TEXT_PRIMARY,
                ModernIcons.createBackIcon(20, ModernUI.TEXT_PRIMARY));
        backButton.setPreferredSize(new Dimension(260, 48));
        backButton.setCornerRadius(24);
        backButton.addActionListener(e -> {
            new WelcomeScreen();
            dispose();
        });
        bottomBar.add(backButton);

        mainPanel.add(bottomBar, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    private void refreshCategoryTabLocks() {
        boolean normalUnlocked = GameProgress.isTierUnlocked(2);
        boolean hardUnlocked = GameProgress.isTierUnlocked(3);

        normalTab.setEnabled(normalUnlocked);
        normalTab.setIcon(normalUnlocked ? null : ModernIcons.createLockIcon(14, ModernUI.TEXT_MUTED));
        normalTab.setToolTipText(normalUnlocked ? null : "Pass Level 5 to unlock Normal levels");

        hardTab.setEnabled(hardUnlocked);
        hardTab.setIcon(hardUnlocked ? null : ModernIcons.createLockIcon(14, ModernUI.TEXT_MUTED));
        hardTab.setToolTipText(hardUnlocked ? null : "Pass Level 10 to unlock Hard levels");
    }

    private void selectTier(int tier) {
        this.activeTier = tier;
        GameProgress.Difficulty diff = (tier == 1) ? GameProgress.Difficulty.EASY :
                                        (tier == 2 ? GameProgress.Difficulty.NORMAL : GameProgress.Difficulty.HARD);
        GameProgress.setDifficulty(diff);

        easyTab.setBaseColor(tier == 1 ? ModernUI.ACCENT_EMERALD : ModernUI.BTN_SECONDARY_BASE);
        easyTab.setHoverColor(tier == 1 ? ModernUI.ACCENT_EMERALD : ModernUI.BTN_SECONDARY_HOVER);

        normalTab.setBaseColor(tier == 2 ? ModernUI.ACCENT_PURPLE_START : ModernUI.BTN_SECONDARY_BASE);
        normalTab.setHoverColor(tier == 2 ? ModernUI.ACCENT_HOVER_START : ModernUI.BTN_SECONDARY_HOVER);

        hardTab.setBaseColor(tier == 3 ? ModernUI.ACCENT_ROSE : ModernUI.BTN_SECONDARY_BASE);
        hardTab.setHoverColor(tier == 3 ? ModernUI.ACCENT_ROSE : ModernUI.BTN_SECONDARY_HOVER);

        easyTab.repaint();
        normalTab.repaint();
        hardTab.repaint();

        int highestUnlocked = GameProgress.getHighestUnlockedLevel();
        progressLabel.setText("Highest Unlocked: Level " + Math.min(highestUnlocked, 15) + " of " + GameProgress.getTotalLevels());

        int startLevel = (tier - 1) * 5 + 1;

        for (int i = 0; i < 5; i++) {
            int levelNumber = startLevel + i;
            boolean unlocked = GameProgress.isUnlocked(levelNumber);
            RoundedButton btn = levelButtons[i];

            if (unlocked) {
                String name = LEVEL_NAMES[levelNumber - 1];
                btn.setText("<html><center><b>LEVEL " + levelNumber + "</b><br><font size='3' color='#A5F3FC'>" + name + "</font></center></html>");
                btn.setIcon(null);
                btn.setBaseColor(ModernUI.ACCENT_PURPLE_START);
                btn.setHoverColor(ModernUI.ACCENT_HOVER_START);
                btn.setGradientEndColor(ModernUI.ACCENT_PURPLE_END);
                btn.setFont(ModernUI.FONT_BODY);
                btn.setToolTipText(null);
                btn.setEnabled(true);
            } else {
                btn.setText("<html><center><font color='#94A3B8'>LEVEL " + levelNumber + "</font></center></html>");
                btn.setIcon(ModernIcons.createLockIcon(32, ModernUI.TEXT_MUTED));
                btn.setBaseColor(ModernUI.CARD_BG);
                btn.setHoverColor(ModernUI.CARD_BG);
                btn.setGradientEndColor(null);
                btn.setToolTipText("Pass Level " + (levelNumber - 1) + " to unlock");
                btn.setEnabled(false);
            }
            btn.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int startLevelIndex = (activeTier - 1) * 5;
        for (int i = 0; i < 5; i++) {
            if (e.getSource() == levelButtons[i]) {
                int levelIndex = startLevelIndex + i;
                if (levelIndex < LEVEL_LAUNCHERS.size()) {
                    LEVEL_LAUNCHERS.get(levelIndex).get();
                    dispose();
                    return;
                }
            }
        }
    }
}

