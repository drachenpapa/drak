package de.drachenpapa.drak.game.logic;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all players and their associated curves.
 * Provides methods for player state, round reset, and score handling.
 */
public class PlayerManager {

    @Getter
    private final List<Player> players;
    @Getter
    private final List<Point[]> curvePoints = new ArrayList<>();

    PlayerManager(List<Player> players) {
        this.players = players;
    }

    int getAlivePlayerCount() {
        int aliveCount = 0;
        for (Player player : players) {
            if (player.isAlive()) {
                aliveCount++;
            }
        }
        return aliveCount;
    }

    void resetForNextRound() {
        curvePoints.clear();
        for (Player player : players) {
            player.setCurve(new Curve(
                    (int) (Math.random() * GameEngine.WINDOW_WIDTH) + 100,
                    (int) (Math.random() * GameEngine.WINDOW_HEIGHT) + 100,
                    Math.random() * 360,
                    (int) (Math.random() * 10) + 1)
            );
            player.setAlive(true);
        }
    }

    void increasePointsForAlivePlayers() {
        players.stream()
                .filter(Player::isAlive)
                .forEach(Player::increaseScore);
    }
}
