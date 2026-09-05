import java.awt.Color;

/**
 * Level 9: Normal — Timer: 35s, Objective: Score >= 650.
 */
public class Level9 extends AbstractLevel {

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

    public Level9() {
        super(9, "Speed Score 650", SHAPE, 10,
                ConstraintType.TIMER, 35,
                ObjectiveType.SCORE, 650,
                null, 0,
                IMAGES, Color.BLUE, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level10();
    }
}
