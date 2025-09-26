package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PlayerManagerTest {

    private List<Player> players;
    private PlayerManager playerManager;

    @BeforeEach
    public void setUp() {
        players = List.of(
                new Player("Player 1", Color.RED, '1', 'q'),
                new Player("Player 2", Color.GREEN, 'y', 'x'));
        playerManager = new PlayerManager(players);
    }

    @Test
    public void testGetAlivePlayerCount() {
        players.get(0).setAlive(true);
        players.get(1).setAlive(false);
        assertThat("Should return 1 when only one player is alive", playerManager.getAlivePlayerCount(), is(1));
        players.get(1).setAlive(true);
        assertThat("Should return 2 when both players are alive", playerManager.getAlivePlayerCount(), is(2));
    }

    @Test
    public void testResetForNextRoundResetsCurvesAndAliveState() {
        players.get(0).setAlive(false);
        players.get(1).setAlive(false);
        players.get(0).setCurve(new Curve(1, 1, 0, 1));
        players.get(1).setCurve(new Curve(2, 2, 0, 1));

        playerManager.resetForNextRound();

        assertThat("All players should be alive after reset", players.get(0).isAlive() && players.get(1).isAlive(), is(true));
        assertThat("Curve should be reset for player 1", players.get(0).getCurve() != null, is(true));
        assertThat("Curve should be reset for player 2", players.get(1).getCurve() != null, is(true));
    }

    @Test
    public void testIncreasePointsForAlivePlayers() {
        players.get(0).setAlive(true);
        players.get(1).setAlive(false);
        int initialScore = players.get(0).getScore();

        playerManager.increasePointsForAlivePlayers();

        assertThat("Only alive players should get a point", players.get(0).getScore(), is(initialScore + 1));
        assertThat("Dead players should not get a point", players.get(1).getScore(), is(0));
    }
}

