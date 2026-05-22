package de.drachenpapa.drak.game.logic;

import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.random.RandomGenerator;

/**
 * Immutable player configuration used before creating runtime Player instances.
 */
public record PlayerConfig(String playerName, Color color, char leftKey, char rightKey) {

    public static final int MAX_PLAYER_NAME_LENGTH = 20;

    private static final Set<Byte> BIDI_CONTROL_DIRECTIONALITIES = Set.of(
        Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE,
        Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING,
        Character.DIRECTIONALITY_RIGHT_TO_LEFT_ISOLATE,
        Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE,
        Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING,
        Character.DIRECTIONALITY_LEFT_TO_RIGHT_ISOLATE,
        Character.DIRECTIONALITY_POP_DIRECTIONAL_ISOLATE,
        Character.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT
    );

    public PlayerConfig {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be blank.");
        }
        if (playerName.length() > MAX_PLAYER_NAME_LENGTH) {
            throw new IllegalArgumentException("Player name must not exceed %d characters.".formatted(MAX_PLAYER_NAME_LENGTH));
        }
        if (containsBidiControlChar(playerName)) {
            throw new IllegalArgumentException("Player name must not contain Unicode direction control characters.");
        }
        Objects.requireNonNull(color, "Player color must not be null.");
        if (Character.isWhitespace(leftKey) || Character.isWhitespace(rightKey)) {
            throw new IllegalArgumentException("Control keys must not be whitespace.");
        }
        if (leftKey == rightKey) {
            throw new IllegalArgumentException("Left and right control keys must differ.");
        }
    }

    private static boolean containsBidiControlChar(String name) {
        return name.codePoints()
            .mapToObj(Character::getDirectionality)
            .anyMatch(BIDI_CONTROL_DIRECTIONALITIES::contains);
    }

    Player toPlayer() {
        return new Player(playerName, color, leftKey, rightKey);
    }

    Player toPlayer(int tickIntervalMs) {
        return new Player(playerName, color, leftKey, rightKey, tickIntervalMs, RandomGenerator.getDefault());
    }
}
