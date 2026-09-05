import java.awt.Color;

/**
 * Level 14: Hard — Moves: 16, Objective: Score >= 550.
 */
public class Level14 extends AbstractLevel {

    private static final int[][] SHAPE = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}
    };

    private static final String[] IMAGES = {
            "/pizza.png", "/burger.png", "/french fries.png",
            "/sandwich.png", "/hot dog.png", "/cookies.png"
    };

    public Level14() {
        super(14, "Score Rush 550", SHAPE, 10,
                ConstraintType.MOVES, 16,
                ObjectiveType.SCORE, 550,
                null, 0,
                IMAGES, Color.ORANGE, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level15();
    }
}
