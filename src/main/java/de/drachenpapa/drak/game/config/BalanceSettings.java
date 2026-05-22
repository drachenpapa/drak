package de.drachenpapa.drak.game.config;

/**
 * Game balancing constants for scoring and speed.
 */
public final class BalanceSettings {

    public static final int POINTS_PER_OPPONENT = 10;
    public static final int SPEED_DELAY_MULTIPLIER = 10;
    public static final int SPEED_LEVEL_INVERSION_OFFSET = 6;
    public static final int DEFAULT_SPEED_LEVEL = 3;
    public static final int DEFAULT_TICK_INTERVAL_MS = SPEED_DELAY_MULTIPLIER * (SPEED_LEVEL_INVERSION_OFFSET - DEFAULT_SPEED_LEVEL);

    private BalanceSettings() {
    }
}
