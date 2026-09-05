import java.awt.Color;

/**
 * Level 11: Hard — Timer: 30s, Objective: Score >= 500.
 */
public class Level11 extends AbstractLevel {

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
            "/pineapple.png", "/strawberry.png", "/orange.png"
    };

    public Level11() {
        super(11, "Speed Score 500", SHAPE, 10,
                ConstraintType.TIMER, 30,
                ObjectiveType.SCORE, 500,
                null, 0,
                IMAGES, Color.YELLOW, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level12();
    }
}
