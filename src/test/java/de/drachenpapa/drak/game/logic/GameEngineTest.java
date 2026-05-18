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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Timer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("constructors")
    class Constructors {

        @Test
        @DisplayName("creates window when initialized with GameConfig")
        void createsWindowWhenInitializedWithGameConfig() {
            clearInvocations(gameWindowFactory);
            GameConfig config = new GameConfig(3, List.of(
                    new PlayerConfig("Player 1", Color.RED, '1', 'q'),
                    new PlayerConfig("Player 2", Color.GREEN, 'y', 'x')
            ));

            GameEngine engine = new GameEngine(config, gameWindowFactory);

            verify(gameWindowFactory).create(any(), any());
            assertThat(ReflectionTestUtils.getField(engine, "gameSpeed")).isEqualTo(30);
            GameStateManager stateManager = (GameStateManager) ReflectionTestUtils.getField(engine, "gameStateManager");
            assertThat(ReflectionTestUtils.getField(stateManager, "winningScore")).isEqualTo(10);
        }

        @Test
        @DisplayName("computes winning score from player count")
        void computesWinningScoreFromPlayerCount() {
            GameConfig config = new GameConfig(3, List.of(
                    new PlayerConfig("One", Color.RED, '1', 'q'),
                    new PlayerConfig("Two", Color.GREEN, 'y', 'x'),
                    new PlayerConfig("Three", Color.BLUE, 'b', 'n'),
                    new PlayerConfig("Four", Color.YELLOW, '4', '5')
            ));

            GameEngine engine = new GameEngine(config, gameWindowFactory);

            GameStateManager stateManager = (GameStateManager) ReflectionTestUtils.getField(engine, "gameStateManager");
            assertThat(ReflectionTestUtils.getField(stateManager, "winningScore")).isEqualTo(30);
        }
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
            verify(gameStateManager).stopTimers();
            verify(gameWindow).close();
        }

        @Test
        @DisplayName("stops running game timer before closing")
        void stopsRunningGameTimerBeforeClosing() {
            Timer timer = org.mockito.Mockito.mock(Timer.class);
            ReflectionTestUtils.setField(gameEngine, "gameTimer", timer);

            gameEngine.quitGame();

            verify(timer).stop();
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

        @Test
        @DisplayName("repaints panel when timer tick runs in RUNNING state")
        void repaintsPanelOnTimerTickInRunningState() {
            when(gameStateManager.getGameState()).thenReturn(GameState.RUNNING, GameState.RUNNING);
            Player firstPlayer = gameEngine.getPlayers().getFirst();
            int beforeX = firstPlayer.getCurve().getXPosition();
            int beforeY = firstPlayer.getCurve().getYPosition();

            gameEngine.startGame();
            Timer timer = (Timer) ReflectionTestUtils.getField(gameEngine, "gameTimer");
            assertThat(timer).isNotNull();

            for (ActionListener listener : timer.getActionListeners()) {
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "tick"));
            }

            verify(gamePanel, times(1)).repaint();
            assertThat(firstPlayer.getCurve().getXPosition()).isNotEqualTo(beforeX);
            assertThat(firstPlayer.getCurve().getYPosition()).isNotEqualTo(beforeY);
        }

        @Test
        @DisplayName("does not update or repaint on PAUSED state")
        void doesNotUpdateOrRepaintOnPausedState() {
            when(gameStateManager.getGameState()).thenReturn(GameState.PAUSED);
            Player firstPlayer = gameEngine.getPlayers().getFirst();
            int beforeX = firstPlayer.getCurve().getXPosition();
            int beforeY = firstPlayer.getCurve().getYPosition();

            gameEngine.startGame();
            Timer timer = (Timer) ReflectionTestUtils.getField(gameEngine, "gameTimer");
            assertThat(timer).isNotNull();

            for (ActionListener listener : timer.getActionListeners()) {
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "tick"));
            }

            verify(gamePanel, never()).repaint();
            assertThat(firstPlayer.getCurve().getXPosition()).isEqualTo(beforeX);
            assertThat(firstPlayer.getCurve().getYPosition()).isEqualTo(beforeY);
        }
    }
}
