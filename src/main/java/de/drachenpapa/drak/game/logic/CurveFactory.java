package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.CurvePhysicsSettings;
import de.drachenpapa.drak.game.config.CurveSpawnSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;

import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Creates curve instances with randomized spawn configuration.
 */
final class CurveFactory {

    private CurveFactory() {
    }


    static Curve createRandomCurve(RandomGenerator randomGenerator) {
        RandomGenerator rng = Objects.requireNonNull(randomGenerator, "randomGenerator");
        int xPosition = rng.nextInt(DisplaySettings.WINDOW_WIDTH) + CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        int yPosition = rng.nextInt(DisplaySettings.WINDOW_HEIGHT) + CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        double direction = rng.nextDouble(CurvePhysicsSettings.ANGLE_FULL_CIRCLE);
        int gapInterval = rng.nextInt(CurveSpawnSettings.MIN_INITIAL_GAP_INTERVAL, CurveSpawnSettings.MAX_INITIAL_GAP_INTERVAL + 1);
        return new Curve(xPosition, yPosition, direction, gapInterval, rng);
    }
}
