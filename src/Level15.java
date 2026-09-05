import java.awt.Color;

/**
 * Level 15: Hard — Timer: 20s, Objective: Collect 15 Donuts.
 */
public class Level15 extends AbstractLevel {

    private static final int[][] SHAPE = {
            {0, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0, 1, 1, 1, 1, 1, 1, 0}
    };

    private static final String[] IMAGES = {
            "/donuts.png", "/cake.png", "/cupcake.png",
            "/ice cream.png", "/apple pie.png", "/pancake.png"
    };

    public Level15() {
        super(15, "Final Boss Donuts", SHAPE, 10,
                ConstraintType.TIMER, 20,
                ObjectiveType.COLLECT, 0,
                "/donuts.png", 15,
                IMAGES, Color.MAGENTA, 80, 300);
    }

    @Override
    protected String winMessage() {
        return "CONGRATULATIONS!\nYou defeated the Final Boss and passed ALL 15 Levels!\nScore: " + score +
                "\nDifficulty: " + GameProgress.getDifficulty().getLabel() +
                "\nThank you for playing Tile-Matching!";
    }

    @Override
    protected String[] winOptions() {
        return new String[]{"Play Again", "Level Menu", "Quit"};
    }

    @Override
    protected void openNextScreen() {
        new LevelWindow();
    }
}
