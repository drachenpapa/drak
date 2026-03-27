package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Color;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("GameStateManager")
@ExtendWith(MockitoExtension.class)
class GameStateManagerTest {

    @Mock
    private PlayerManager playerManager;

    private GameStateManager gameStateManager;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        players = List.of(
                new Player("Player 1", Color.RED, '1', 'q'),
                new Player("Player 2", Color.GREEN, 'y', 'x'));
        gameStateManager = new GameStateManager(playerManager, 3);
    }

    @Nested
    @DisplayName("checkForGameEnd()")
    class CheckForGameEnd {

        @BeforeEach
        void stubPlayers() {
            when(playerManager.getPlayers()).thenReturn(players);
        }

        @Test
        @DisplayName("transitions to GAME_OVER when a player reaches winning score")
        void transitionsToGameOverOnWinningScore() {
            players.getFirst().setScore(3);
            gameStateManager.checkForGameEnd();
            assertThat(gameStateManager.getGameState()).isEqualTo(GameState.GAME_OVER);
        }

        @Test
        @DisplayName("transitions to READY_FOR_NEXT_ROUND when only one player is alive")
        void transitionsToReadyForNextRoundWithOneAlivePlayer() {
            players.get(0).setAlive(true);
            players.get(1).setAlive(false);

            gameStateManager.checkForGameEnd();

            assertThat(gameStateManager.getGameState()).isEqualTo(GameState.READY_FOR_NEXT_ROUND);
        }

        @Test
        @DisplayName("remains RUNNING when no end condition is met")
        void remainsRunningWithoutEndCondition() {
            players.get(0).setScore(1);
            players.get(1).setScore(1);
            players.get(0).setAlive(true);
            players.get(1).setAlive(true);
            when(playerManager.getAlivePlayerCount()).thenReturn(2);

            gameStateManager.setGameState(GameState.RUNNING);
            gameStateManager.checkForGameEnd();

            assertThat(gameStateManager.getGameState()).isEqualTo(GameState.RUNNING);
        }
    }

    @Nested
    @DisplayName("handleRoundTransition()")
    class HandleRoundTransition {

        @Test
        @DisplayName("sets state to PAUSED")
        void setsStateToPaused() {
            gameStateManager.setGameState(GameState.READY_FOR_NEXT_ROUND);

            gameStateManager.handleRoundTransition(() -> {});

            assertThat(gameStateManager.getGameState()).isEqualTo(GameState.PAUSED);
        }
    }
}
