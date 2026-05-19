package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.DisplaySettings;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Manages all players and their associated curves.
 * Provides methods for player state, round reset, and score handling.
 * Maintains an occupancy grid for O(1) collision detection.
 */
public class PlayerManager {

    @Getter
    private final List<Player> players;
    private final int[][] occupiedGrid = new int[DisplaySettings.PLAY_AREA_WIDTH][DisplaySettings.PLAY_AREA_HEIGHT];

    PlayerManager(List<Player> players) {
        this.players = players;
    }

    int getAlivePlayerCount() {
        return (int) players.stream().filter(Player::isAlive).count();
    }

    int getTrailOwner(int x, int y) {
        return occupiedGrid[x][y];
    }

    void markTrailOwner(int x, int y, int ownerId) {
        occupiedGrid[x][y] = ownerId;
    }

    int[][] getOccupiedGrid() {
        return occupiedGrid;
    }

    void resetForNextRound() {
        for (int[] row : occupiedGrid) {
            Arrays.fill(row, 0);
        }
        for (Player player : players) {
            player.resetCurve();
            player.setAlive(true);
        }
    }

    void increasePointsForAlivePlayers() {
        players.stream()
            .filter(Player::isAlive)
            .forEach(Player::increaseScore);
    }
}
