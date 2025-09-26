package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.*;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameEngineTest {

    private final GameStateManager gameStateManager = mock(GameStateManager.class);
    private final GamePanel gamePanel = mock(GamePanel.class);
    private final GameWindow gameWindow = mock(GameWindow.class);
    private final GameWindowFactory gameWindowFactory = mock(GameWindowFactory.class);

    private GameEngine gameEngine;

    @BeforeEach
    public void setUp() {
        when(gameWindowFactory.create(any(), any())).thenReturn(gameWindow);
        List<Player> players = List.of(
                new Player("Player 1", Color.RED, '1', 'q'),
                new Player("Player 2", Color.GREEN, 'y', 'x'));
        gameEngine = new GameEngine(players, 3, gameWindowFactory);

        ReflectionTestUtils.setField(gameEngine, "gameStateManager", gameStateManager);
        ReflectionTestUtils.setField(gameEngine, "gamePanel", gamePanel);
    }

    @Test
    public void testHandleRoundTransitionCallsGameStateManagerAndRepaint() {
        gameEngine.handleRoundTransition();
        verify(gameStateManager).handleRoundTransition(any());
        verify(gamePanel).repaint();
    }

    @Test
    public void testQuitGameCallsGameStateManagerAndGameWindow() {
        gameEngine.quitGame();
        verify(gameStateManager).setGameState(GameState.GAME_OVER);
        verify(gameWindow).close();
    }

    @Test
    public void testStartGameSetsGameState() {
        gameEngine.startGame();
        verify(gameStateManager).setGameState(GameState.RUNNING);
    }
}
