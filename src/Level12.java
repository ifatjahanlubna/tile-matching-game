import java.awt.Color;

/**
 * Level 12: Hard — Moves: 15, Objective: Collect 15 Pizza slices.
 */
public class Level12 extends AbstractLevel {

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

    public Level12() {
        super(12, "Collect Pizza", SHAPE, 10,
                ConstraintType.MOVES, 15,
                ObjectiveType.COLLECT, 0,
                "/pizza.png", 15,
                IMAGES, Color.RED, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level13();
    }
}
