import java.awt.Color;

/**
 * Level 13: Hard — Timer: 25s, Objective: Clear all obstacles.
 */
public class Level13 extends AbstractLevel {

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

    public Level13() {
        super(13, "Clear Board", SHAPE, 10,
                ConstraintType.TIMER, 25,
                ObjectiveType.CLEAR_ALL_OBSTACLES, 0,
                null, 0,
                IMAGES, Color.GRAY, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level14();
    }
}
