/*
Manages player progression and level unlock state across difficulty modes.
 */
public final class GameProgress {

    public enum Difficulty {
        EASY("Easy", 1.30, 0.75, 1.30, 0.0),
        NORMAL("Normal", 1.00, 1.00, 1.00, 1.0),
        HARD("Hard", 0.75, 1.30, 0.75, 1.5);

        private final String label;
        private final double timeMultiplier;
        private final double scoreMultiplier;
        private final double movesMultiplier;
        private final double obstacleMultiplier;

        Difficulty(String label, double timeMultiplier, double scoreMultiplier, double movesMultiplier,
                double obstacleMultiplier) {
            this.label = label;
            this.timeMultiplier = timeMultiplier;
            this.scoreMultiplier = scoreMultiplier;
            this.movesMultiplier = movesMultiplier;
            this.obstacleMultiplier = obstacleMultiplier;
        }

        public String getLabel() {
            return label;
        }

        public double getTimeMultiplier() {
            return timeMultiplier;
        }

        public double getScoreMultiplier() {
            return scoreMultiplier;
        }

        public double getMovesMultiplier() {
            return movesMultiplier;
        }

        public double getObstacleMultiplier() {
            return obstacleMultiplier;
        }
    }

    private static final int TOTAL_LEVELS = 15;
    private static Difficulty currentDifficulty = Difficulty.EASY;

    // Track overall highest unlocked level (1 to 15)
    private static int highestUnlockedLevel = 1;

    private GameProgress() {
    }

    public static boolean isUnlocked(int levelNumber) {
        return levelNumber <= highestUnlockedLevel;
    }

    public static void unlock(int levelNumber) {
        if (levelNumber > highestUnlockedLevel) {
            highestUnlockedLevel = Math.min(levelNumber, TOTAL_LEVELS + 1);
        }
    }

    public static int getHighestUnlockedLevel() {
        return highestUnlockedLevel;
    }

    public static int getTotalLevels() {
        return TOTAL_LEVELS;
    }

    public static Difficulty getDifficulty() {
        return currentDifficulty;
    }

    public static void setDifficulty(Difficulty difficulty) {
        if (difficulty != null) {
            currentDifficulty = difficulty;
        }
    }

    public static boolean isDifficultyUnlocked(Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return true;
            case NORMAL: return highestUnlockedLevel >= 6;
            case HARD: return highestUnlockedLevel >= 11;
            default: return false;
        }
    }

    public static boolean isTierUnlocked(int tier) {
        if (tier == 1) return true;
        if (tier == 2) return highestUnlockedLevel >= 6;
        if (tier == 3) return highestUnlockedLevel >= 11;
        return false;
    }
}
