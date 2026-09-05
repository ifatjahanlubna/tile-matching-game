import java.awt.Color;

/**
 * Level 4: Easy — Moves: 20, Objective: Clear all Ice tiles.
 */
public class Level4 extends AbstractLevel {

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

    public Level4() {
        super(4, "Clear the Ice", SHAPE, 10,
                ConstraintType.MOVES, 20,
                ObjectiveType.CLEAR_ICE, 0,
                null, 0,
                IMAGES, Color.CYAN, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level5();
    }
}
