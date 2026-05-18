package de.drachenpapa.drak.game.logic;

import lombok.Getter;
import lombok.Setter;

/**
 * Manages the current game state and transitions.
 * Provides methods for state changes and round handling.
 */
public class GameStateManager {

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
            roundTransitionTimer = new javax.swing.Timer(1000, e -> {
                resetRound.run();
                gameState = GameState.RUNNING;
                stopTimers();
            });
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
