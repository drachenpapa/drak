package de.drachenpapa.drak.game.logic;

/**
 * Enum representing the possible states of the game.
 * Used for controlling the game flow and transitions.
 */
public enum GameState {
    STARTED,
    RUNNING,
    PAUSED,
    READY_FOR_NEXT_ROUND,
    GAME_OVER
}
