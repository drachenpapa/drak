package de.drachenpapa.drak.game.logic;

import java.util.List;

/**
 * Immutable game configuration used to initialize a game session.
 */
public record GameConfig(int speed, List<PlayerConfig> playerConfigs) {

    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 5;

    public GameConfig {
        if (speed < MIN_SPEED || speed > MAX_SPEED) {
            throw new IllegalArgumentException("Speed must be between %d and %d.".formatted(MIN_SPEED, MAX_SPEED));
        }
        playerConfigs = List.copyOf(playerConfigs);
        if (playerConfigs.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required.");
        }
    }

    List<Player> createPlayers() {
        return playerConfigs.stream().map(PlayerConfig::toPlayer).toList();
    }
}
