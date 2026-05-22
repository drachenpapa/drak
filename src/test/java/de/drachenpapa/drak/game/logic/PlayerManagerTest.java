package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
            players.get(0).revive();
            players.get(1).kill();
            assertThat(playerManager.getAlivePlayerCount()).isEqualTo(1);

            players.get(1).revive();
            assertThat(playerManager.getAlivePlayerCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("resetForNextRound()")
    class ResetForNextRound {

        @Test
        @DisplayName("sets all players alive and resets their curves")
        void setsAllPlayersAliveAndResetsCurves() {
            players.get(0).kill();
            players.get(1).kill();
            players.get(0).setCurve(new Curve(1, 1, 0, 1));
            players.get(1).setCurve(new Curve(2, 2, 0, 1));
            Curve firstBefore = players.get(0).getCurve();
            Curve secondBefore = players.get(1).getCurve();
            playerManager.markTrailOwner(10, 10, 1);

            playerManager.resetForNextRound();

            assertAll(
                () -> assertThat(players)
                    .extracting(Player::isAlive)
                    .containsOnly(true),
                () -> assertThat(players)
                    .extracting(Player::getCurve)
                    .doesNotContainNull(),
                () -> assertThat(players.getFirst().getCurve())
                    .isNotSameAs(firstBefore),
                () -> assertThat(players.get(1).getCurve())
                    .isNotSameAs(secondBefore),
                () -> assertThat(playerManager.getTrailOwner(10, 10))
                    .isZero()
            );
        }
    }

    @Nested
    @DisplayName("increasePointsForAlivePlayers()")
    class IncreasePointsForAlivePlayers {

        @Test
        @DisplayName("only increments score for alive players")
        void onlyIncrementsScoreForAlivePlayers() {
            players.get(0).revive();
            players.get(1).kill();
            int initialScore = players.get(0).getScore();

            playerManager.increasePointsForAlivePlayers();

            assertAll(
                () -> assertThat(players.getFirst().getScore()).isEqualTo(initialScore + 1),
                () -> assertThat(players.get(1).getScore()).isZero()
            );
        }
    }
}
