import java.awt.Color;

/**
 * Level 3: Easy — Timer: 50s, Objective: Collect 12 Cherries.
 */
public class Level3 extends AbstractLevel {

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
            "/cherry.png", "/apple.png", "/mango.png",
            "/grape.png", "/strawberry.png", "/orange.png", "/banana.png"
    };

    public Level3() {
        super(3, "Collect Cherries", SHAPE, 10,
                ConstraintType.TIMER, 50,
                ObjectiveType.COLLECT, 0,
                "/cherry.png", 12,
                IMAGES, Color.RED, 100, 400);
    }

    @Override
    protected void openNextScreen() {
        new Level4();
    }
}
