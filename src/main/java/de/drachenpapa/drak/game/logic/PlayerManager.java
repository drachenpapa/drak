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
        return (int) players.stream().filter(Player::isAlive).count();
    }

    void resetForNextRound() {
        curvePoints.clear();
        for (Player player : players) {
            player.setCurve(CurveFactory.createRandomCurve());
            player.setAlive(true);
        }
    }

    void increasePointsForAlivePlayers() {
        players.stream()
                .filter(Player::isAlive)
                .forEach(Player::increaseScore);
    }
}
