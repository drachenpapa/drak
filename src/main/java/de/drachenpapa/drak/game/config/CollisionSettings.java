package de.drachenpapa.drak.game.config;

/**
 * Collision detection constants.
 */
public final class CollisionSettings {

    public static final int CURVE_WIDTH = 3;
    public static final int SELF_COLLISION_SKIP = 10;
    public static final int MAX_INTERPOLATION_STEPS_PER_TICK = 12;

    public static final int GRACE_ZONE_CAPACITY = SELF_COLLISION_SKIP * (MAX_INTERPOLATION_STEPS_PER_TICK + 1);

    private CollisionSettings() {
    }
}
