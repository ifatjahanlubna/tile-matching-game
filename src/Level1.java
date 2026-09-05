import java.awt.Color;

/**
 * Level 1: Easy — Timer: 60s, Objective: Score >= 600.
 */
public class Level1 extends AbstractLevel {

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

    public Level1() {
        super(1, "Score Rush", SHAPE, 10,
                ConstraintType.TIMER, 60,
                ObjectiveType.SCORE, 600,
                null, 0,
                IMAGES, Color.BLACK, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level2();
    }
}
