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

    static Curve createRandomCurve(RandomGenerator randomGenerator, int tickIntervalMs) {
        var rng = Objects.requireNonNull(randomGenerator, "randomGenerator");
        var safeWidth = DisplaySettings.PLAY_AREA_WIDTH - 2 * CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        var safeHeight = DisplaySettings.PLAY_AREA_HEIGHT - 2 * CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        var xPosition = rng.nextInt(safeWidth) + CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        var yPosition = rng.nextInt(safeHeight) + CurveSpawnSettings.SPAWN_POSITION_OFFSET;
        var direction = rng.nextDouble(CurvePhysicsSettings.ANGLE_FULL_CIRCLE);
        var gapIntervalMs = rng.nextLong(CurveSpawnSettings.MIN_INITIAL_GAP_INTERVAL_MS, CurveSpawnSettings.MAX_INITIAL_GAP_INTERVAL_MS + 1);
        var gapIntervalTicks = Math.max(1, (int) (gapIntervalMs / Math.max(1, tickIntervalMs)));
        return new Curve(xPosition, yPosition, direction, gapIntervalTicks, tickIntervalMs, rng);
    }
}
