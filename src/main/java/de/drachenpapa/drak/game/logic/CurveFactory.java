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
        int safeWidth = DisplaySettings.PLAY_AREA_WIDTH - 2 * CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        int safeHeight = DisplaySettings.PLAY_AREA_HEIGHT - 2 * CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        int xPosition = rng.nextInt(safeWidth) + CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        int yPosition = rng.nextInt(safeHeight) + CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        double direction = rng.nextDouble(CurvePhysicsSettings.ANGLE_FULL_CIRCLE);
        long gapInterval = rng.nextLong(CurveSpawnSettings.MIN_INITIAL_GAP_INTERVAL_MS, CurveSpawnSettings.MAX_INITIAL_GAP_INTERVAL_MS + 1);
        return new Curve(xPosition, yPosition, direction, gapInterval, rng);
    }
}
