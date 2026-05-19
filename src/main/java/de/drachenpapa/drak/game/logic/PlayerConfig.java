package de.drachenpapa.drak.game.logic;

import java.awt.*;
import java.util.Objects;

/**
 * Immutable player configuration used before creating runtime Player instances.
 */
public record PlayerConfig(String playerName, Color color, char leftKey, char rightKey) {

    public PlayerConfig {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be blank.");
        }
        Objects.requireNonNull(color, "Player color must not be null.");
        if (Character.isWhitespace(leftKey) || Character.isWhitespace(rightKey)) {
            throw new IllegalArgumentException("Control keys must not be whitespace.");
        }
        if (leftKey == rightKey) {
            throw new IllegalArgumentException("Left and right control keys must differ.");
        }
    }

    Player toPlayer() {
        return new Player(playerName, color, leftKey, rightKey);
    }
}
