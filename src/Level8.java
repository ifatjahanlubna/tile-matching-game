import java.awt.Color;

/**
 * Level 8: Normal — Moves: 18, Objective: Collect 12 Oranges.
 */
public class Level8 extends AbstractLevel {

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
            "/orange.png", "/apple.png", "/mango.png",
            "/grape.png", "/strawberry.png", "/banana.png"
    };

    public Level8() {
        super(8, "Collect Oranges", SHAPE, 10,
                ConstraintType.MOVES, 18,
                ObjectiveType.COLLECT, 0,
                "/orange.png", 12,
                IMAGES, Color.MAGENTA, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level9();
    }
}
