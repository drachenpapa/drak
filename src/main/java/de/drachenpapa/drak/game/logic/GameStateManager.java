package de.drachenpapa.drak.game.logic;

import lombok.Getter;
import lombok.Setter;

/**
 * Manages the current game state and transitions.
 * Provides methods for state changes and round handling.
 */
public class GameStateManager {

    private static final int ROUND_TRANSITION_DELAY_MS = 1_000;

    private final int winningScore;
    private final PlayerManager playerManager;
    private javax.swing.Timer roundTransitionTimer;

    @Setter
    @Getter
    private GameState gameState = GameState.STARTED;

    GameStateManager(PlayerManager playerManager, int winningScore) {
        this.playerManager = playerManager;
        this.winningScore = winningScore;
    }

    void checkForGameEnd() {
        for (Player player : playerManager.getPlayers()) {
            if (player.getScore() >= winningScore) {
                gameState = GameState.GAME_OVER;
                return;
            }
        }
        if (playerManager.getAlivePlayerCount() <= 1) {
            gameState = GameState.READY_FOR_NEXT_ROUND;
        }
    }

    void handleRoundTransition(Runnable resetRound) {
        if (gameState == GameState.READY_FOR_NEXT_ROUND) {
            gameState = GameState.PAUSED;
            stopTimers();
            roundTransitionTimer = new javax.swing.Timer(ROUND_TRANSITION_DELAY_MS, e -> {
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
