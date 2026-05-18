package de.drachenpapa.drak.game.logic;

import java.util.random.RandomGenerator;

/**
 * Creates curve instances with randomized spawn configuration.
 */
final class CurveFactory {

    private static final int SPAWN_POSITION_OFFSET = 100;
    private static final int MIN_GAP_INTERVAL = 1;
    private static final int MAX_GAP_INTERVAL = 10;
    private static final RandomGenerator RNG = RandomGenerator.getDefault();

    private CurveFactory() {
    }

    static Curve createRandomCurve() {
        int xPosition = RNG.nextInt(GameEngine.WINDOW_WIDTH) + SPAWN_POSITION_OFFSET;
        int yPosition = RNG.nextInt(GameEngine.WINDOW_HEIGHT) + SPAWN_POSITION_OFFSET;
        double direction = RNG.nextDouble(360);
        int gapInterval = RNG.nextInt(MIN_GAP_INTERVAL, MAX_GAP_INTERVAL + 1);
        return new Curve(xPosition, yPosition, direction, gapInterval);
    }
}
