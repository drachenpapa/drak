package de.drachenpapa.drak.game.config;

/**
 * Curve physics constants including movement, rotation, gaps, and point storage.
 */
public final class CurvePhysicsSettings {

    public static final long MAX_GAP_INTERVAL_MS = 6_000L;
    public static final long MIN_GAP_INTERVAL_MS = 1_000L;
    public static final int MIN_GAP_LENGTH = 2;
    public static final int MAX_GAP_LENGTH = 4;
    public static final double STEP_SIZE = 6.0;
    public static final double TURN_ANGLE_DEGREES = 10.0;
    public static final double ANGLE_FULL_CIRCLE = 360.0;
    public static final int MAX_STORED_POINTS = 50_000;

    private CurvePhysicsSettings() {
    }
}
