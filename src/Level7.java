import java.awt.Color;

/**
 * Level 7: Normal — Timer: 40s, Objective: Clear all Locked tiles.
 */
public class Level7 extends AbstractLevel {

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

    public Level7() {
        super(7, "Break the Locks", SHAPE, 10,
                ConstraintType.TIMER, 40,
                ObjectiveType.CLEAR_LOCKS, 0,
                null, 0,
                IMAGES, Color.DARK_GRAY, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level8();
    }
}
