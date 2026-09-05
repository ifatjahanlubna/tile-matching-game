import java.awt.Color;

/**
 * Level 6: Normal — Moves: 22, Objective: Score >= 700.
 */
public class Level6 extends AbstractLevel {

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
            "/apple.png", "/orange.png", "/mango.png",
            "/grape.png", "/strawberry.png", "/banana.png"
    };

    public Level6() {
        super(6, "Score Rush 700", SHAPE, 10,
                ConstraintType.MOVES, 22,
                ObjectiveType.SCORE, 700,
                null, 0,
                IMAGES, Color.ORANGE, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level7();
    }
}
