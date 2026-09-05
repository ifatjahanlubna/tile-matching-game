import java.awt.Color;

/**
 * Level 2: Easy — Moves: 25, Objective: Score >= 600.
 */
public class Level2 extends AbstractLevel {

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
            "/apple.png", "/mango.png", "/grape.png",
            "/pineapple.png", "/strawberry.png", "/orange.png", "/banana.png"
    };

    public Level2() {
        super(2, "Score Rush, tighter", SHAPE, 10,
                ConstraintType.MOVES, 25,
                ObjectiveType.SCORE, 600,
                null, 0,
                IMAGES, Color.BLUE, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level3();
    }
}
