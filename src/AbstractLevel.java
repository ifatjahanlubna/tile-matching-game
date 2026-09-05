import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Base game screen for all levels.
 * Supports single constraint (Timer or Moves) and single objective (Score, Collect, Clear Obstacles).
 */
public abstract class AbstractLevel extends JFrame implements ActionListener {

    public enum ConstraintType {
        TIMER, MOVES
    }

    public enum ObjectiveType {
        SCORE, COLLECT, CLEAR_ICE, CLEAR_LOCKS, CLEAR_ALL_OBSTACLES
    }

    protected final int levelNumber;
    protected final String levelName;
    protected final int[][] shape;
    protected final int rows;
    protected final int cols;
    protected final int points;
    protected final Color gridBackground;
    protected final int topGap;
    protected final int swapDelayMs;
    protected final String[] availableImages;

    // Single Constraint & Single Objective config
    protected final ConstraintType constraintType;
    protected final ObjectiveType objectiveType;
    protected final int constraintValue;
    protected final int targetScore;
    protected final int passScore;
    protected final String targetIngredient;
    protected final int targetCollectCount;

    protected final Map<String, Integer> collectedCounts = new HashMap<>();

    protected int time;
    protected int movesLeft;
    protected int score = 0;
    protected Timer timer;
    protected int selectedRow = -1;
    protected int selectedCol = -1;
    protected boolean animating = false;

    // Obstacle states: 0 = Normal, 1 = Ice, 2 = Double Chain
    protected int[][] obstacleState;

    protected int[][] dropYOffset;

    // Tile scale/opacity modifier for vanishing animation
    protected float[][] tileOpacity;
    protected float boardAlpha = 0.0f; // Entrance fade-in alpha

    protected JLabel timeInfo;
    protected JLabel scoreLabel;
    protected JLabel movesLabel;
    protected JPanel objectivesHud;
    protected RoundedButton restart;
    protected RoundedButton exit;
    protected RoundedButton menu;
    protected JPanel gridPanel;
    protected JLabel[][] tiles;
    protected String[][] imagePaths;

    protected final Random random = new Random();

    // Primary Single Constraint + Single Objective Constructor
    protected AbstractLevel(int levelNumber, String levelName, int[][] shape, int points,
                             ConstraintType constraintType, int constraintValue,
                             ObjectiveType objectiveType, int targetScore,
                             String targetIngredient, int targetCollectCount,
                             String[] availableImages, Color gridBackground, int topGap, int swapDelayMs) {
        this.levelNumber = levelNumber;
        this.levelName = (levelName != null && !levelName.trim().isEmpty()) ? levelName : "Level " + levelNumber;
        this.shape = shape;
        this.rows = shape.length;
        this.cols = shape[0].length;
        this.points = points;

        this.constraintType = constraintType;
        this.objectiveType = objectiveType;
        this.constraintValue = constraintValue;

        GameProgress.Difficulty diff = GameProgress.getDifficulty();

        if (constraintType == ConstraintType.TIMER) {
            this.time = Math.max(10, (int) Math.round(constraintValue * diff.getTimeMultiplier()));
            this.movesLeft = 999;
        } else {
            this.time = 999;
            this.movesLeft = Math.max(5, (int) Math.round(constraintValue * diff.getMovesMultiplier()));
        }

        this.targetScore = targetScore;
        this.passScore = (targetScore > 0) ? (int) Math.round(targetScore * diff.getScoreMultiplier()) : 0;
        this.targetIngredient = targetIngredient;
        this.targetCollectCount = targetCollectCount;

        if (targetIngredient != null) {
            collectedCounts.put(targetIngredient, 0);
        }

        this.availableImages = availableImages;
        this.gridBackground = gridBackground;
        this.topGap = topGap;
        this.swapDelayMs = swapDelayMs;

        this.tiles = new JLabel[rows][cols];
        this.imagePaths = new String[rows][cols];
        this.obstacleState = new int[rows][cols];
        this.tileOpacity = new float[rows][cols];
        this.dropYOffset = new int[rows][cols];

        initUI();
        populateGridWithoutMatches();
        applyObstacles();
        updateObjectivesHUD();
        startEntranceAnimation();
        if (constraintType == ConstraintType.TIMER) {
            startTimer();
        }
        setVisible(true);
    }

    protected abstract void openNextScreen();

    public boolean isGoalAchieved() {
        switch (objectiveType) {
            case SCORE:
                return score >= passScore;
            case COLLECT:
                return targetIngredient != null && collectedCounts.getOrDefault(targetIngredient, 0) >= targetCollectCount;
            case CLEAR_ICE:
                return countIce() == 0;
            case CLEAR_LOCKS:
                return countLocks() == 0;
            case CLEAR_ALL_OBSTACLES:
                return countObstacles() == 0;
            default:
                return false;
        }
    }

    protected int countIce() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (shape[r][c] == 1 && obstacleState[r][c] == 1) count++;
            }
        }
        return count;
    }

    protected int countLocks() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (shape[r][c] == 1 && obstacleState[r][c] == 2) count++;
            }
        }
        return count;
    }

    protected int countObstacles() {
        return countIce() + countLocks();
    }

    protected String winMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Level Passed: ").append(levelName).append("\n");
        sb.append("Score: ").append(score);
        if (constraintType == ConstraintType.MOVES) {
            sb.append("\nMoves Left: ").append(movesLeft);
        } else {
            sb.append("\nTime Remaining: ").append(time).append("s");
        }
        sb.append("\nDifficulty: ").append(GameProgress.getDifficulty().getLabel());
        return sb.toString();
    }

    protected String[] winOptions() {
        return new String[]{"Play Again", "Next Level", "Quit"};
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 820);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("Tile Matching - L" + levelNumber + ": " + levelName + " (" + GameProgress.getDifficulty().getLabel() + ")");

        JPanel mainContent = new JPanel(new BorderLayout()) {
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

        // Top Header Section (HUD + Objectives)
        JPanel topHeaderArea = new JPanel(new BorderLayout());
        topHeaderArea.setOpaque(false);

        // Top HUD Bar
        JPanel topHud = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ModernUI.enableGraphicsAntialiasing(g2);
                g2.setColor(ModernUI.HUD_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(ModernUI.CARD_BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        topHud.setPreferredSize(new Dimension(800, 52));
        topHud.setOpaque(false);

        JLabel levelLabel = new JLabel("LVL " + levelNumber);
        levelLabel.setFont(ModernUI.FONT_HEADER);
        levelLabel.setForeground(ModernUI.ACCENT_CYAN);
        topHud.add(createPillBadge(levelLabel, 85, 36));

        GameProgress.Difficulty diff = GameProgress.getDifficulty();
        JLabel diffLabel = new JLabel(diff.getLabel());
        diffLabel.setFont(ModernUI.FONT_SMALL);
        diffLabel.setForeground(diff == GameProgress.Difficulty.HARD ? ModernUI.ACCENT_ROSE :
                                (diff == GameProgress.Difficulty.EASY ? ModernUI.ACCENT_EMERALD : ModernUI.ACCENT_PURPLE_START));
        topHud.add(createPillBadge(diffLabel, 80, 36));

        if (constraintType == ConstraintType.MOVES) {
            movesLabel = new JLabel("Moves: " + movesLeft);
            movesLabel.setFont(ModernUI.FONT_BODY);
            movesLabel.setForeground(ModernUI.TEXT_PRIMARY);
            movesLabel.setIcon(ModernIcons.createMoveIcon(18, ModernUI.ACCENT_CYAN));
            movesLabel.setIconTextGap(4);
            topHud.add(createPillBadge(movesLabel, 130, 36));
        } else {
            timeInfo = new JLabel(time + "s");
            timeInfo.setFont(ModernUI.FONT_HEADER);
            timeInfo.setForeground(ModernUI.TEXT_PRIMARY);
            timeInfo.setIcon(ModernIcons.createTimerIcon(20, ModernUI.ACCENT_CYAN));
            timeInfo.setIconTextGap(4);
            topHud.add(createPillBadge(timeInfo, 100, 36));
        }

        scoreLabel = new JLabel(passScore > 0 ? "Score: " + score + " / " + passScore : "Score: " + score);
        scoreLabel.setFont(ModernUI.FONT_BODY);
        scoreLabel.setForeground(ModernUI.ACCENT_GOLD);
        scoreLabel.setIcon(ModernIcons.createScoreIcon(18, ModernUI.ACCENT_GOLD));
        scoreLabel.setIconTextGap(4);
        topHud.add(createPillBadge(scoreLabel, passScore > 0 ? 190 : 130, 36));

        topHeaderArea.add(topHud, BorderLayout.NORTH);

        // Live Objectives Bar
        objectivesHud = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        objectivesHud.setOpaque(false);
        objectivesHud.setPreferredSize(new Dimension(800, 36));
        topHeaderArea.add(objectivesHud, BorderLayout.SOUTH);

        mainContent.add(topHeaderArea, BorderLayout.NORTH);

        // Center Grid Area
        JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        gridWrapper.setOpaque(false);

        gridPanel = new JPanel(new GridLayout(rows, cols, 4, 4));
        gridPanel.setOpaque(false);
        gridPanel.setPreferredSize(new Dimension(600, 580));

        gridWrapper.add(gridPanel);
        mainContent.add(gridWrapper, BorderLayout.CENTER);

        // Bottom Navigation Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ModernUI.enableGraphicsAntialiasing(g2);
                g2.setColor(ModernUI.HUD_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(ModernUI.CARD_BORDER);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        bottomBar.setPreferredSize(new Dimension(800, 55));
        bottomBar.setOpaque(false);

        restart = new RoundedButton("Restart", ModernUI.BTN_SECONDARY_BASE, ModernUI.BTN_SECONDARY_HOVER,
                ModernUI.TEXT_PRIMARY, ModernIcons.createRestartIcon(18, ModernUI.TEXT_PRIMARY));
        restart.setPreferredSize(new Dimension(140, 38));
        restart.setFont(ModernUI.FONT_BUTTON);
        restart.setCornerRadius(19);
        restart.addActionListener(this);
        bottomBar.add(restart);

        menu = new RoundedButton("Menu", ModernUI.BTN_SECONDARY_BASE, ModernUI.BTN_SECONDARY_HOVER,
                ModernUI.TEXT_PRIMARY, ModernIcons.createMenuIcon(18, ModernUI.TEXT_PRIMARY));
        menu.setPreferredSize(new Dimension(140, 38));
        menu.setFont(ModernUI.FONT_BUTTON);
        menu.setCornerRadius(19);
        menu.addActionListener(this);
        bottomBar.add(menu);

        exit = new RoundedButton("Exit", ModernUI.BTN_SECONDARY_BASE, ModernUI.BTN_SECONDARY_HOVER,
                ModernUI.TEXT_PRIMARY, ModernIcons.createExitIcon(18, ModernUI.TEXT_PRIMARY));
        exit.setPreferredSize(new Dimension(140, 38));
        exit.setFont(ModernUI.FONT_BUTTON);
        exit.setCornerRadius(19);
        exit.addActionListener(this);
        bottomBar.add(exit);

        mainContent.add(bottomBar, BorderLayout.SOUTH);
        add(mainContent);
    }

    protected void updateObjectivesHUD() {
        if (objectivesHud == null) return;
        objectivesHud.removeAll();

        switch (objectiveType) {
            case SCORE: {
                boolean done = score >= passScore;
                JLabel l = new JLabel((done ? "✔ " : "") + "Objective: Score ≥ " + passScore + " (" + score + "/" + passScore + ")");
                l.setFont(ModernUI.FONT_BODY);
                l.setForeground(done ? ModernUI.ACCENT_EMERALD : ModernUI.TEXT_PRIMARY);
                objectivesHud.add(createPillBadge(l, 320, 28));
                break;
            }
            case COLLECT: {
                int current = collectedCounts.getOrDefault(targetIngredient, 0);
                boolean done = current >= targetCollectCount;
                String name = getIngredientName(targetIngredient);
                JLabel l = new JLabel((done ? "✔ " : "") + "Objective: Collect " + targetCollectCount + " " + name + " (" + Math.min(current, targetCollectCount) + "/" + targetCollectCount + ")");
                l.setFont(ModernUI.FONT_BODY);
                l.setForeground(done ? ModernUI.ACCENT_EMERALD : ModernUI.TEXT_PRIMARY);
                objectivesHud.add(createPillBadge(l, 340, 28));
                break;
            }
            case CLEAR_ICE: {
                int ice = countIce();
                boolean done = ice == 0;
                JLabel l = new JLabel(done ? "✔ All Ice Cleared!" : "Objective: Clear all Ice tiles (" + ice + " left)");
                l.setFont(ModernUI.FONT_BODY);
                l.setForeground(done ? ModernUI.ACCENT_EMERALD : ModernUI.ACCENT_CYAN);
                objectivesHud.add(createPillBadge(l, 320, 28));
                break;
            }
            case CLEAR_LOCKS: {
                int locks = countLocks();
                boolean done = locks == 0;
                JLabel l = new JLabel(done ? "✔ All Locks Broken!" : "Objective: Clear all Locked tiles (" + locks + " left)");
                l.setFont(ModernUI.FONT_BODY);
                l.setForeground(done ? ModernUI.ACCENT_EMERALD : ModernUI.ACCENT_ROSE);
                objectivesHud.add(createPillBadge(l, 340, 28));
                break;
            }
            case CLEAR_ALL_OBSTACLES: {
                int obs = countObstacles();
                boolean done = obs == 0;
                JLabel l = new JLabel(done ? "✔ Board Cleared!" : "Objective: Clear all obstacles (" + obs + " left)");
                l.setFont(ModernUI.FONT_BODY);
                l.setForeground(done ? ModernUI.ACCENT_EMERALD : ModernUI.ACCENT_ROSE);
                objectivesHud.add(createPillBadge(l, 340, 28));
                break;
            }
        }

        objectivesHud.revalidate();
        objectivesHud.repaint();
    }

    private String getIngredientName(String imagePath) {
        if (imagePath == null) return "Item";
        String file = imagePath.substring(imagePath.lastIndexOf('/') + 1).replace(".png", "");
        file = Character.toUpperCase(file.charAt(0)) + file.substring(1);
        if (!file.endsWith("s") && !file.endsWith("es") && !file.contains(" ")) {
            file += "s";
        }
        return file;
    }

    private JPanel createPillBadge(JLabel contentLabel, int width, int height) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, (height - 24) / 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ModernUI.enableGraphicsAntialiasing(g2);
                g2.setColor(ModernUI.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), height, height);
                g2.setColor(ModernUI.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, height, height);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(width, height));
        badge.add(contentLabel);
        return badge;
    }

    private void populateGridWithoutMatches() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tileOpacity[r][c] = 1.0f;
                dropYOffset[r][c] = 0;
                if (shape[r][c] == 1) {
                    String imagePath;
                    do {
                        imagePath = availableImages[random.nextInt(availableImages.length)];
                    } while (createsInitialMatch(r, c, imagePath));

                    imagePaths[r][c] = imagePath;
                    JLabel tile = createTileLabel(r, c, imagePath);
                    tile.addMouseListener(new TileClickListener(r, c));
                    tiles[r][c] = tile;
                    gridPanel.add(tile);
                } else {
                    JLabel socket = new JLabel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            ModernUI.enableGraphicsAntialiasing(g2);
                            g2.setColor(new Color(15, 23, 42, 100));
                            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 16, 16);
                            g2.dispose();
                        }
                    };
                    socket.setOpaque(false);
                    gridPanel.add(socket);
                    tiles[r][c] = null;
                    imagePaths[r][c] = null;
                    obstacleState[r][c] = 0;
                }
            }
        }
    }

    private boolean createsInitialMatch(int r, int c, String imagePath) {
        if (c >= 2 && imagePaths[r][c - 1] != null && imagePaths[r][c - 2] != null &&
            imagePaths[r][c - 1].equals(imagePath) && imagePaths[r][c - 2].equals(imagePath)) {
            return true;
        }
        if (r >= 2 && imagePaths[r - 1][c] != null && imagePaths[r - 2][c] != null &&
            imagePaths[r - 1][c].equals(imagePath) && imagePaths[r - 2][c].equals(imagePath)) {
            return true;
        }
        return false;
    }

    private void startEntranceAnimation() {
        animating = true;
        boardAlpha = 0.0f;
        Timer animTimer = new Timer(25, null);
        animTimer.addActionListener(e -> {
            boardAlpha += 0.1f;
            if (boardAlpha >= 1.0f) {
                boardAlpha = 1.0f;
                animTimer.stop();
                animating = false;
            }
            gridPanel.repaint();
        });
        animTimer.start();
    }

    private void applyObstacles() {
        int targetObstacles = 0;
        if (objectiveType == ObjectiveType.CLEAR_ICE) targetObstacles = 10;
        else if (objectiveType == ObjectiveType.CLEAR_LOCKS) targetObstacles = 8;
        else if (objectiveType == ObjectiveType.CLEAR_ALL_OBSTACLES) targetObstacles = 12;

        if (targetObstacles > 0) {
            int placed = 0;
            int maxAttempts = 300;
            int attempts = 0;
            while (placed < targetObstacles && attempts < maxAttempts) {
                attempts++;
                int r = random.nextInt(rows);
                int c = random.nextInt(cols);
                if (shape[r][c] == 1 && obstacleState[r][c] == 0) {
                    if (objectiveType == ObjectiveType.CLEAR_LOCKS || (objectiveType == ObjectiveType.CLEAR_ALL_OBSTACLES && random.nextBoolean())) {
                        obstacleState[r][c] = 2; // Double Locked
                        if (tiles[r][c] != null) tiles[r][c].setToolTipText("Chained Padlock! Match adjacent tiles to break");
                    } else {
                        obstacleState[r][c] = 1; // Ice
                        if (tiles[r][c] != null) tiles[r][c].setToolTipText("Frozen Ice! Match adjacent tiles to break");
                    }
                    placed++;
                }
            }
        }
    }

    private JLabel createTileLabel(int r, int c, String imagePath) {
        JLabel tile = new JLabel(resizedIcon(imagePath), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ModernUI.enableGraphicsAntialiasing(g2);

                // Smooth falling translation offset
                if (dropYOffset[r][c] != 0) {
                    g2.translate(0, dropYOffset[r][c]);
                }

                float currentAlpha = boardAlpha * tileOpacity[r][c];
                if (currentAlpha < 1.0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.0f, Math.min(1.0f, currentAlpha))));
                }

                int state = obstacleState[r][c];
                int w = getWidth();
                int h = getHeight();

                if (state == 2) {
                    // Candy Crush Heavy Metallic Chained Lock Theme
                    g2.setColor(new Color(30, 41, 59, 220));
                    g2.fillRoundRect(0, 0, w, h, 16, 16);

                    // Metallic Double Chains
                    g2.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(71, 85, 105, 180)); // Shadow chain
                    g2.drawLine(6, 6, w - 6, h - 6);
                    g2.drawLine(w - 6, 6, 6, h - 6);

                    g2.setColor(new Color(226, 232, 240, 240)); // Silver metallic chain
                    g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(6, 6, w - 6, h - 6);
                    g2.drawLine(w - 6, 6, 6, h - 6);

                    // Central Golden Padlock
                    int cx = w / 2;
                    int cy = h / 2;

                    // Padlock Shackle
                    g2.setColor(new Color(241, 245, 249));
                    g2.setStroke(new BasicStroke(3.5f));
                    g2.drawArc(cx - 10, cy - 14, 20, 18, 0, 180);

                    // Padlock Body
                    GradientPaint lockGrad = new GradientPaint(cx - 12, cy - 4, new Color(250, 204, 21), cx + 12, cy + 16, new Color(202, 138, 4));
                    g2.setPaint(lockGrad);
                    g2.fillRoundRect(cx - 13, cy - 4, 26, 20, 8, 8);
                    g2.setColor(new Color(161, 98, 7));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(cx - 13, cy - 4, 26, 20, 8, 8);

                    // Keyhole
                    g2.setColor(new Color(15, 23, 42));
                    g2.fillOval(cx - 3, cy + 1, 6, 6);
                    g2.fillRect(cx - 2, cy + 4, 4, 6);

                } else if (state == 1) {
                    // Ice Frost Theme
                    g2.setColor(new Color(186, 230, 253, 220));
                    g2.fillRoundRect(0, 0, w, h, 16, 16);

                    // Crystalline Ice Cracks
                    g2.setColor(new Color(255, 255, 255, 220));
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawLine(8, 12, w - 12, h - 16);
                    g2.drawLine(w / 2, 8, w / 2 + 10, h - 10);
                    g2.drawLine(10, h / 2, w - 10, h / 2 + 6);
                } else {
                    g2.setColor(new Color(255, 255, 255, 240));
                    g2.fillRoundRect(0, 0, w, h, 16, 16);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        tile.setOpaque(false);
        tile.setBorder(createTileBorder(r, c, false));
        return tile;
    }

    private Border createTileBorder(int r, int c, boolean selected) {
        return new Border() {
            @Override
            public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                ModernUI.enableGraphicsAntialiasing(g2);

                float currentAlpha = boardAlpha * tileOpacity[r][c];
                if (currentAlpha < 1.0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.0f, Math.min(1.0f, currentAlpha))));
                }

                if (selected) {
                    // Vibrant Glowing Red/Rose Selection Border for maximum clarity
                    g2.setColor(new Color(239, 68, 68)); // Bright Rose Red
                    g2.setStroke(new BasicStroke(4.0f));
                    g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 16, 16);

                    g2.setColor(new Color(254, 226, 226, 220));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(x + 3, y + 3, width - 7, height - 7, 12, 12);
                } else if (obstacleState[r][c] == 1) {
                    g2.setColor(new Color(56, 189, 248)); // Ice Cyan Border
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 16, 16);
                } else if (obstacleState[r][c] == 2) {
                    g2.setColor(new Color(225, 29, 72)); // Metallic Red Lock Border
                    g2.setStroke(new BasicStroke(2.8f));
                    g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 16, 16);
                } else {
                    g2.setColor(ModernUI.CARD_BORDER);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(x, y, width - 1, height - 1, 16, 16);
                }
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component comp) {
                return new Insets(3, 3, 3, 3);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }

    private ImageIcon resizedIcon(String path) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image img = icon.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private boolean playable(int r, int c) {
        return shape[r][c] == 1 && imagePaths[r][c] != null;
    }

    private boolean isAdjacent(int r1, int c1, int r2, int c2) {
        return shape[r1][c1] == 1 && shape[r2][c2] == 1 &&
                ((Math.abs(r1 - r2) == 1 && c1 == c2) || (Math.abs(c1 - c2) == 1 && r1 == r2));
    }

    private void swapTiles(int r1, int c1, int r2, int c2) {
        String tmpPath = imagePaths[r1][c1];
        imagePaths[r1][c1] = imagePaths[r2][c2];
        imagePaths[r2][c2] = tmpPath;

        int tmpState = obstacleState[r1][c1];
        obstacleState[r1][c1] = obstacleState[r2][c2];
        obstacleState[r2][c2] = tmpState;

        tiles[r1][c1].setIcon(resizedIcon(imagePaths[r1][c1]));
        tiles[r2][c2].setIcon(resizedIcon(imagePaths[r2][c2]));
        tiles[r1][c1].repaint();
        tiles[r2][c2].repaint();

        if (constraintType == ConstraintType.MOVES) {
            decrementMoves();
        }
    }

    private void decrementMoves() {
        if (movesLeft > 0) {
            movesLeft--;
            if (movesLabel != null) {
                movesLabel.setText("Moves: " + movesLeft);
                if (movesLeft <= 5) {
                    movesLabel.setForeground(ModernUI.ACCENT_ROSE);
                    movesLabel.setIcon(ModernIcons.createMoveIcon(18, ModernUI.ACCENT_ROSE));
                }
            }
        }
    }

    private class TileClickListener extends MouseAdapter {
        private final int r;
        private final int c;

        TileClickListener(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (animating) return;
            if (constraintType == ConstraintType.MOVES && movesLeft <= 0) return;
            if (constraintType == ConstraintType.TIMER && time <= 0) return;

            // Locked/Frozen tiles CANNOT be selected to swap
            if (obstacleState[r][c] > 0) return;

            if (selectedRow == -1) {
                selectedRow = r;
                selectedCol = c;
                tiles[r][c].setBorder(createTileBorder(r, c, true));
            } else {
                if (isAdjacent(selectedRow, selectedCol, r, c) && obstacleState[selectedRow][selectedCol] == 0) {
                    swapTiles(selectedRow, selectedCol, r, c);
                    Timer delay = new Timer(swapDelayMs, e2 -> checkMatchesWithAnimation());
                    delay.setRepeats(false);
                    delay.start();
                }
                tiles[selectedRow][selectedCol].setBorder(createTileBorder(selectedRow, selectedCol, false));
                selectedRow = -1;
            }
        }
    }

    private void checkMatchesWithAnimation() {
        boolean[][] matched = new boolean[rows][cols];
        boolean matchFound = false;

        for (int r = 0; r <= rows - 3; r++) {
            for (int c = 0; c < cols; c++) {
                if (playable(r, c) && playable(r + 1, c) && playable(r + 2, c) &&
                        imagePaths[r][c].equals(imagePaths[r + 1][c]) &&
                        imagePaths[r][c].equals(imagePaths[r + 2][c])) {
                    matched[r][c] = matched[r + 1][c] = matched[r + 2][c] = true;
                    matchFound = true;
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols - 3; c++) {
                if (playable(r, c) && playable(r, c + 1) && playable(r, c + 2) &&
                        imagePaths[r][c].equals(imagePaths[r][c + 1]) &&
                        imagePaths[r][c].equals(imagePaths[r][c + 2])) {
                    matched[r][c] = matched[r][c + 1] = matched[r][c + 2] = true;
                    matchFound = true;
                }
            }
        }

        if (matchFound) {
            animating = true;

            Timer vanishTimer = new Timer(35, null);
            final int[] step = {0};

            vanishTimer.addActionListener(e -> {
                step[0]++;
                float alpha = 1.0f - (step[0] * 0.10f);

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if (matched[r][c]) {
                            tileOpacity[r][c] = Math.max(0.0f, alpha);
                            if (tiles[r][c] != null) tiles[r][c].repaint();
                        }
                    }
                }

                if (step[0] >= 10) {
                    vanishTimer.stop();

                    int cleared = 0;
                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < cols; c++) {
                            if (matched[r][c]) {
                                cleared++;
                                String path = imagePaths[r][c];
                                if (objectiveType == ObjectiveType.COLLECT && path != null && path.equals(targetIngredient)) {
                                    collectedCounts.put(path, collectedCounts.getOrDefault(path, 0) + 1);
                                }
                                imagePaths[r][c] = null;
                                tileOpacity[r][c] = 1.0f;
                                if (tiles[r][c] != null) {
                                    tiles[r][c].setIcon(null);
                                    tiles[r][c].repaint();
                                }
                                damageAdjacentObstacles(r, c);
                            }
                        }
                    }

                    dropTiles();
                    refillTiles();
                    setScore(score + cleared * points);
                    updateObjectivesHUD();

                    if (isGoalAchieved()) {
                        animating = false;
                        checkGameEndConditions();
                        return;
                    }

                    // Smooth, visually clear Candy Crush-style cascading delay & falling animation
                    animateFallingTiles(() -> {
                        Timer cascadeTimer = new Timer(400, e2 -> {
                            ((Timer) e2.getSource()).stop();
                            checkMatchesWithAnimation();
                        });
                        cascadeTimer.setRepeats(false);
                        cascadeTimer.start();
                    });
                }
            });
            vanishTimer.start();
        } else {
            animating = false;
            updateObjectivesHUD();
            if (isGoalAchieved() || (constraintType == ConstraintType.MOVES && movesLeft <= 0)) {
                checkGameEndConditions();
            }
        }
    }

    /** Smooth, eye-catching tile falling animation for cascade drops. */
    private void animateFallingTiles(Runnable onComplete) {
        boolean hasOffsets = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (dropYOffset[r][c] != 0) {
                    hasOffsets = true;
                    break;
                }
            }
        }

        if (!hasOffsets) {
            onComplete.run();
            return;
        }

        Timer dropTimer = new Timer(18, null);
        dropTimer.addActionListener(e -> {
            boolean active = false;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (dropYOffset[r][c] != 0) {
                        int step = Math.max(5, Math.abs(dropYOffset[r][c]) / 5);
                        dropYOffset[r][c] += step;
                        if (dropYOffset[r][c] >= 0) {
                            dropYOffset[r][c] = 0;
                        } else {
                            active = true;
                        }
                    }
                }
            }
            gridPanel.repaint();
            if (!active) {
                dropTimer.stop();
                onComplete.run();
            }
        });
        dropTimer.start();
    }

    private void damageAdjacentObstacles(int r, int c) {
        int[][] neighbors = {{r - 1, c}, {r + 1, c}, {r, c - 1}, {r, c + 1}};
        for (int[] n : neighbors) {
            int nr = n[0];
            int nc = n[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && shape[nr][nc] == 1) {
                if (obstacleState[nr][nc] > 0) {
                    obstacleState[nr][nc]--;
                    if (obstacleState[nr][nc] == 0 && tiles[nr][nc] != null) {
                        tiles[nr][nc].setToolTipText(null);
                    }
                    if (tiles[nr][nc] != null) tiles[nr][nc].repaint();
                }
            }
        }
    }

    /**
     * Drops tiles downwards.
     * Locked/Ice obstacle tiles (obstacleState > 0) REMAIN ANCHORED IN PLACE and never fall down!
     */
    private void dropTiles() {
        for (int c = 0; c < cols; c++) {
            for (int r = rows - 1; r >= 0; r--) {
                if (shape[r][c] == 0) continue;

                // Locked/Ice obstacles NEVER move down!
                if (obstacleState[r][c] > 0) continue;

                if (imagePaths[r][c] == null) {
                    int k = r - 1;
                    // Skip invalid cells and anchored obstacle tiles
                    while (k >= 0 && (shape[k][c] == 0 || obstacleState[k][c] > 0 || imagePaths[k][c] == null)) {
                        k--;
                    }
                    if (k >= 0) {
                        int delta = r - k;
                        imagePaths[r][c] = imagePaths[k][c];
                        tiles[r][c].setIcon(tiles[k][c].getIcon());
                        dropYOffset[r][c] = -delta * 68; // Smooth falling offset

                        imagePaths[k][c] = null;
                        tiles[k][c].setIcon(null);
                    }
                }
            }
        }
    }

    private void refillTiles() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (shape[r][c] == 1 && obstacleState[r][c] == 0 && imagePaths[r][c] == null) {
                    String newImage = availableImages[random.nextInt(availableImages.length)];
                    imagePaths[r][c] = newImage;
                    tileOpacity[r][c] = 1.0f;
                    dropYOffset[r][c] = -68; // Falling from top
                    tiles[r][c].setIcon(resizedIcon(newImage));
                    tiles[r][c].repaint();
                }
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int newScore) {
        this.score = newScore;
        if (scoreLabel != null) {
            scoreLabel.setText(passScore > 0 ? "Score: " + score + " / " + passScore : "Score: " + score);
        }
    }

    private void checkGameEndConditions() {
        if (timer != null) timer.stop();
        if (isGoalAchieved()) {
            GameProgress.unlock(levelNumber + 1);
            int choice = ModernDialog.showOptions(this, "Level Passed!", winMessage(), winOptions(), 1);

            if (choice == 0) {
                restartLevel();
            } else if (choice == 1) {
                dispose();
                openNextScreen();
            } else {
                System.exit(0);
            }
        } else {
            String reason = (constraintType == ConstraintType.MOVES && movesLeft <= 0) ? "Out of moves!" : "Time's up!";
            String[] options = {"Try Again", "Quit"};
            int choice = ModernDialog.showOptions(this, "Level Failed",
                    reason + "\n" + winMessage() + "\nTry again?",
                    options, 0);

            if (choice == 0) {
                restartLevel();
            } else {
                System.exit(0);
            }
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            if (time > 0) {
                time--;
                if (timeInfo != null) {
                    timeInfo.setText(time + "s");
                    if (time <= 10) {
                        timeInfo.setForeground(ModernUI.ACCENT_ROSE);
                        timeInfo.setIcon(ModernIcons.createTimerIcon(20, ModernUI.ACCENT_ROSE));
                    }
                }
            } else {
                checkGameEndConditions();
            }
        });
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == restart) {
            restartLevel();
        } else if (e.getSource() == menu) {
            if (timer != null) timer.stop();
            int choice = ModernDialog.showOptions(this, "Leave Level?",
                    "Return to the level menu?\nYour current progress will be lost.",
                    new String[]{"Stay", "Leave"}, 0);
            if (choice == 1) {
                dispose();
                new LevelWindow();
            } else {
                if (timer != null && constraintType == ConstraintType.TIMER) timer.start();
            }
        } else if (e.getSource() == exit) {
            if (timer != null) timer.stop();
            int choice = ModernDialog.showOptions(this, "Exit Game?",
                    "Are you sure you want to quit?",
                    new String[]{"Cancel", "Exit Game"}, 0);
            if (choice == 1) {
                System.exit(0);
            } else {
                if (timer != null && constraintType == ConstraintType.TIMER) timer.start();
            }
        }
    }

    private void restartLevel() {
        score = 0;
        GameProgress.Difficulty diff = GameProgress.getDifficulty();

        if (constraintType == ConstraintType.TIMER) {
            time = Math.max(10, (int) Math.round(constraintValue * diff.getTimeMultiplier()));
            movesLeft = 999;
        } else {
            time = 999;
            movesLeft = Math.max(5, (int) Math.round(constraintValue * diff.getMovesMultiplier()));
        }

        for (String k : collectedCounts.keySet()) {
            collectedCounts.put(k, 0);
        }

        if (scoreLabel != null) {
            scoreLabel.setText(passScore > 0 ? "Score: " + score + " / " + passScore : "Score: " + score);
        }
        if (timeInfo != null && constraintType == ConstraintType.TIMER) {
            timeInfo.setText(time + "s");
            timeInfo.setForeground(ModernUI.TEXT_PRIMARY);
            timeInfo.setIcon(ModernIcons.createTimerIcon(20, ModernUI.ACCENT_CYAN));
        }
        if (movesLabel != null && constraintType == ConstraintType.MOVES) {
            movesLabel.setText("Moves: " + movesLeft);
            movesLabel.setForeground(ModernUI.TEXT_PRIMARY);
            movesLabel.setIcon(ModernIcons.createMoveIcon(18, ModernUI.ACCENT_CYAN));
        }

        gridPanel.removeAll();
        populateGridWithoutMatches();
        applyObstacles();
        updateObjectivesHUD();
        startEntranceAnimation();

        if (timer != null && constraintType == ConstraintType.TIMER) {
            timer.restart();
        }
    }
}
