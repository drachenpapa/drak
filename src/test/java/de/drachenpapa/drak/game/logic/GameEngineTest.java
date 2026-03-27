package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.Color;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("GameEngine")
@ExtendWith(MockitoExtension.class)
class GameEngineTest {

    @Mock
    private GameStateManager gameStateManager;
    @Mock
    private GamePanel gamePanel;
    @Mock
    private GameWindow gameWindow;
    @Mock
    private GameWindowFactory gameWindowFactory;

    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
        when(gameWindowFactory.create(any(), any())).thenReturn(gameWindow);
        List<Player> players = List.of(
                new Player("Player 1", Color.RED, '1', 'q'),
                new Player("Player 2", Color.GREEN, 'y', 'x'));
        gameEngine = new GameEngine(players, 3, gameWindowFactory);

        ReflectionTestUtils.setField(gameEngine, "gameStateManager", gameStateManager);
        ReflectionTestUtils.setField(gameEngine, "gamePanel", gamePanel);
    }

    @Nested
    @DisplayName("handleRoundTransition()")
    class HandleRoundTransition {

        @Test
        @DisplayName("delegates to GameStateManager and repaints panel")
        void delegatesToGameStateManagerAndRepaints() {
            gameEngine.handleRoundTransition();
            verify(gameStateManager).handleRoundTransition(any());
            verify(gamePanel).repaint();
        }
    }

    @Nested
    @DisplayName("quitGame()")
    class QuitGame {

        @Test
        @DisplayName("sets game state to GAME_OVER and closes window")
        void setsGameOverAndClosesWindow() {
            gameEngine.quitGame();
            verify(gameStateManager).setGameState(GameState.GAME_OVER);
            verify(gameWindow).close();
        }
    }

    @Nested
    @DisplayName("startGame()")
    class StartGame {

        @Test
        @DisplayName("sets game state to RUNNING")
        void setsGameStateToRunning() {
            gameEngine.startGame();
            verify(gameStateManager).setGameState(GameState.RUNNING);
        }
    }
}
