package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameStateManagerTest {

    private final PlayerManager playerManager = mock(PlayerManager.class);

    private GameStateManager gameStateManager;
    private List<Player> players;

    @BeforeEach
    public void setUp() {
        players = List.of(
                new Player("Player 1", java.awt.Color.RED, '1', 'q'),
                new Player("Player 2", java.awt.Color.GREEN, 'y', 'x'));
        when(playerManager.getPlayers()).thenReturn(players);
        gameStateManager = new GameStateManager(playerManager, 3);
    }

    @Test
    public void testGameStateTransitionsToGameOverWhenPlayerReachesWinningScore() {
        players.getFirst().setScore(3);
        gameStateManager.checkForGameEnd();
        assertThat("Game state should be GAME_OVER when a player reaches the winning score", gameStateManager.getGameState(), is(GameState.GAME_OVER));
    }

    @Test
    public void testGameStateTransitionsToReadyForNextRoundWhenOnePlayerAlive() {
        players.get(0).setAlive(true);
        players.get(1).setAlive(false);

        gameStateManager.checkForGameEnd();

        assertThat("Game state should be READY_FOR_NEXT_ROUND when only one player is alive", gameStateManager.getGameState(), is(GameState.READY_FOR_NEXT_ROUND));
    }

    @Test
    public void testGameStateRemainsUnchangedIfNoEndConditionMet() {
        players.get(0).setScore(1);
        players.get(1).setScore(1);
        players.get(0).setAlive(true);
        players.get(1).setAlive(true);
        when(playerManager.getAlivePlayerCount()).thenReturn(2);

        gameStateManager.setGameState(GameState.RUNNING);
        gameStateManager.checkForGameEnd();

        assertThat("Game state should remain RUNNING if no end condition is met", gameStateManager.getGameState(), is(GameState.RUNNING));
    }

    @Test
    public void testHandleRoundTransitionSetsPausedAndThenRunning() {
        gameStateManager.setGameState(GameState.READY_FOR_NEXT_ROUND);

        Runnable resetRound = mock(Runnable.class);
        gameStateManager.handleRoundTransition(resetRound);

        assertThat("Game state should be PAUSED after handleRoundTransition is called", gameStateManager.getGameState(), is(GameState.PAUSED));
    }
}
