import java.awt.Color;

/**
 * Level 10: Normal — Moves: 20, Objective: Clear all obstacles (Ice + Locked).
 */
public class Level10 extends AbstractLevel {

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

    public Level10() {
        super(10, "Clear Obstacles", SHAPE, 10,
                ConstraintType.MOVES, 20,
                ObjectiveType.CLEAR_ALL_OBSTACLES, 0,
                null, 0,
                IMAGES, Color.CYAN, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level11();
    }
}
