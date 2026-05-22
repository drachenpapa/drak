package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.BalanceSettings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable game configuration used to initialize a game session.
 */
public record GameConfig(int speed, List<PlayerConfig> playerConfigs) {

    public static final int MIN_SPEED = 1;
    public static final int MAX_SPEED = 5;

    public GameConfig {
        if (speed < MIN_SPEED || speed > MAX_SPEED) {
            throw new IllegalArgumentException("Speed must be between %d and %d.".formatted(MIN_SPEED, MAX_SPEED));
        }
        playerConfigs = List.copyOf(playerConfigs);
        if (playerConfigs.size() < 2) {
            throw new IllegalArgumentException("At least two players are required.");
        }
        validateNoDuplicateControlKeys(playerConfigs);
        validateNoDuplicateNames(playerConfigs);
    }

    private static void validateNoDuplicateControlKeys(List<PlayerConfig> configs) {
        Set<Character> usedKeys = new HashSet<>();
        for (var config : configs) {
            if (!usedKeys.add(config.leftKey())) {
                throw new IllegalArgumentException(
                    "Duplicate control key '%c' assigned to multiple players.".formatted(config.leftKey()));
            }
            if (!usedKeys.add(config.rightKey())) {
                throw new IllegalArgumentException(
                    "Duplicate control key '%c' assigned to multiple players.".formatted(config.rightKey()));
            }
        }
    }

    private static void validateNoDuplicateNames(List<PlayerConfig> configs) {
        Set<String> usedNames = new HashSet<>();
        for (var config : configs) {
            if (!usedNames.add(config.playerName().toLowerCase())) {
                throw new IllegalArgumentException(
                    "Duplicate player name '%s' assigned to multiple players.".formatted(config.playerName()));
            }
        }
    }

    List<Player> createPlayers() {
        return createPlayers(BalanceSettings.DEFAULT_TICK_INTERVAL_MS);
    }

    List<Player> createPlayers(int tickIntervalMs) {
        return playerConfigs.stream().map(c -> c.toPlayer(tickIntervalMs)).toList();
    }
}
