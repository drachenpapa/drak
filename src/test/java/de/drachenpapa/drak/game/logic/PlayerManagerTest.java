package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlayerManager")
class PlayerManagerTest {

    private List<Player> players;
    private PlayerManager playerManager;

    @BeforeEach
    void setUp() {
        players = List.of(
                new Player("Player 1", Color.RED, '1', 'q'),
                new Player("Player 2", Color.GREEN, 'y', 'x'));
        playerManager = new PlayerManager(players);
    }

    @Nested
    @DisplayName("getAlivePlayerCount()")
    class GetAlivePlayerCount {

        @Test
        @DisplayName("reflects changes in alive state")
        void reflectsChangesInAliveState() {
            players.get(0).setAlive(true);
            players.get(1).setAlive(false);
            assertThat(playerManager.getAlivePlayerCount()).isEqualTo(1);

            players.get(1).setAlive(true);
            assertThat(playerManager.getAlivePlayerCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("resetForNextRound()")
    class ResetForNextRound {

        @Test
        @DisplayName("sets all players alive and resets their curves")
        void setsAllPlayersAliveAndResetsCurves() {
            players.get(0).setAlive(false);
            players.get(1).setAlive(false);
            players.get(0).setCurve(new Curve(1, 1, 0, 1));
            players.get(1).setCurve(new Curve(2, 2, 0, 1));

            playerManager.resetForNextRound();

            assertThat(players).extracting(Player::isAlive).containsOnly(true);
            assertThat(players).extracting(Player::getCurve).doesNotContainNull();
        }
    }

    @Nested
    @DisplayName("increasePointsForAlivePlayers()")
    class IncreasePointsForAlivePlayers {

        @Test
        @DisplayName("only increments score for alive players")
        void onlyIncrementsScoreForAlivePlayers() {
            players.get(0).setAlive(true);
            players.get(1).setAlive(false);
            int initialScore = players.get(0).getScore();

            playerManager.increasePointsForAlivePlayers();

            assertThat(players.get(0).getScore()).isEqualTo(initialScore + 1);
            assertThat(players.get(1).getScore()).isEqualTo(0);
        }
    }
}
