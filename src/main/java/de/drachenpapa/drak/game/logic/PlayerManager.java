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
    private final int[][] occupiedGrid;

    PlayerManager(List<Player> players) {
        this(players, DisplaySettings.PLAY_AREA_WIDTH, DisplaySettings.PLAY_AREA_HEIGHT);
    }

    PlayerManager(List<Player> players, int gridWidth, int gridHeight) {
        this.players = players;
        this.occupiedGrid = new int[gridWidth][gridHeight];
        initializePlayers();
    }

    private void initializePlayers() {
        players.forEach(Player::resetCurve);
    }

    int getAlivePlayerCount() {
        return (int) players.stream()
            .filter(Player::isAlive)
            .count();
    }

    int getTrailOwner(int x, int y) {
        if (DisplaySettings.isOutOfBounds(x, y)) {
            return 0;
        }
        return occupiedGrid[x][y];
    }

    void markTrailOwner(int x, int y, int ownerId) {
        if (DisplaySettings.isOutOfBounds(x, y)) {
            throw new IllegalArgumentException(
                "markTrailOwner called with out-of-bounds coordinates (%d, %d)".formatted(x, y));
        }
        occupiedGrid[x][y] = ownerId;
    }

    int[][] getOccupiedGrid() {
        return Arrays.stream(occupiedGrid).map(int[]::clone).toArray(int[][]::new);
    }

    void resetForNextRound() {
        Arrays.stream(occupiedGrid).forEach(row -> Arrays.fill(row, 0));
        players.forEach(player -> {
            player.resetCurve();
            player.revive();
        });
    }

    void increasePointsForAlivePlayers() {
        players.stream()
            .filter(Player::isAlive)
            .forEach(Player::increaseScore);
    }
}
