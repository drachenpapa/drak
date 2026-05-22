package de.drachenpapa.drak.game.logic;

import lombok.Getter;

import javax.swing.*;

/**
 * Manages the current game state and transitions.
 * Provides methods for state changes and round handling.
 */
public class GameStateManager {

    private static final int ROUND_TRANSITION_DELAY_MS = 1_000;

    private final int winningScore;
    private final PlayerManager playerManager;
    private Timer roundTransitionTimer;

    @Getter
    private GameState gameState = GameState.STARTED;

    GameStateManager(PlayerManager playerManager, int winningScore) {
        this.playerManager = playerManager;
        this.winningScore = winningScore;
    }

    void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    void checkForGameEnd() {
        boolean anyPlayerWon = playerManager.getPlayers().stream()
            .anyMatch(player -> player.getScore() >= winningScore);

        if (anyPlayerWon) {
            gameState = GameState.GAME_OVER;
        } else if (playerManager.getAlivePlayerCount() <= 1) {
            gameState = GameState.READY_FOR_NEXT_ROUND;
        }
    }

    void handleRoundTransition(Runnable resetRound) {
        if (gameState == GameState.READY_FOR_NEXT_ROUND) {
            gameState = GameState.PAUSED;
            stopTimers();
            roundTransitionTimer = new Timer(ROUND_TRANSITION_DELAY_MS, e -> {
                try {
                    resetRound.run();
                    gameState = GameState.RUNNING;
                } finally {
                    stopTimers();
                }
            });
            roundTransitionTimer.setRepeats(false);
            roundTransitionTimer.start();
        }
    }

    void stopTimers() {
        if (roundTransitionTimer != null) {
            roundTransitionTimer.stop();
            roundTransitionTimer = null;
        }
    }
}
