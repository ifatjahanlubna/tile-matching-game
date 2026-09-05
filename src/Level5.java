import java.awt.Color;

/**
 * Level 5: Easy — Timer: 45s, Objective: Collect 12 Apples.
 */
public class Level5 extends AbstractLevel {

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
            "/apple.png", "/banana.png", "/pancake.png",
            "/pizza.png", "/cake.png", "/donuts.png", "/ice cream.png"
    };

    public Level5() {
        super(5, "Collect Apples", SHAPE, 10,
                ConstraintType.TIMER, 45,
                ObjectiveType.COLLECT, 0,
                "/apple.png", 12,
                IMAGES, Color.PINK, 80, 300);
    }

    @Override
    protected void openNextScreen() {
        new Level6();
    }
}
