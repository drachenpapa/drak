package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameRenderer;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    @Mock
    private CollisionManager collisionManager;
    @Mock
    private GameRenderer gameRenderer;

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
            assertThat(stateManager).isNotNull();
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
            assertThat(stateManager).isNotNull();
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
            Timer timer = mock(Timer.class);
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
        @DisplayName("stops existing timer before creating a new one")
        void stopsExistingTimerBeforeCreatingNewOne() {
            Timer oldTimer = mock(Timer.class);
            ReflectionTestUtils.setField(gameEngine, "gameTimer", oldTimer);

            gameEngine.startGame();

            verify(oldTimer).stop();
        }

        @Test
        @DisplayName("starts created timer")
        void startsCreatedTimer() {
            gameEngine.startGame();

            Timer timer = (Timer) ReflectionTestUtils.getField(gameEngine, "gameTimer");
            assertThat(timer).isNotNull();
            assertThat(timer.isRunning()).isTrue();
            timer.stop();
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
            assertThat(new Point(firstPlayer.getCurve().getXPosition(), firstPlayer.getCurve().getYPosition()))
                .isNotEqualTo(new Point(beforeX, beforeY));
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

    @Nested
    @DisplayName("internal update flow")
    class InternalUpdateFlow {

        @Test
        @DisplayName("updateGameState moves players when game is not over")
        void updateGameStateMovesPlayersWhenNotGameOver() {
            when(gameStateManager.getGameState()).thenReturn(GameState.RUNNING);
            Player firstPlayer = gameEngine.getPlayers().getFirst();
            int beforePointCount = firstPlayer.getCurve().getPoints().size();

            ReflectionTestUtils.invokeMethod(gameEngine, "updateGameState");

            assertThat(firstPlayer.getCurve().getPoints()).hasSize(beforePointCount + 1);
        }

        @Test
        @DisplayName("updateGameState skips updates when game is over")
        void updateGameStateSkipsWhenGameOver() {
            when(gameStateManager.getGameState()).thenReturn(GameState.GAME_OVER);
            Player firstPlayer = gameEngine.getPlayers().getFirst();
            int beforePointCount = firstPlayer.getCurve().getPoints().size();

            ReflectionTestUtils.invokeMethod(gameEngine, "updateGameState");

            assertThat(firstPlayer.getCurve().getPoints()).hasSize(beforePointCount);
        }

        @Test
        @DisplayName("handleCollision delegates to collision manager and score rendering")
        void handleCollisionDelegatesToCollisionAndScoreRendering() {
            ReflectionTestUtils.setField(gameEngine, "collisionManager", collisionManager);
            ReflectionTestUtils.setField(gameEngine, "gameRenderer", gameRenderer);
            Graphics graphics = mock(Graphics.class);

            ReflectionTestUtils.invokeMethod(gameEngine, "handleCollision", graphics, 0);

            verify(collisionManager).handleCollision(0);
            verify(gameRenderer).drawScorePanel(any(Graphics.class), any(), any());

            ArgumentCaptor<Runnable> gameEndCheck = ArgumentCaptor.forClass(Runnable.class);
            verify(gameRenderer).drawScorePanel(any(Graphics.class), any(), gameEndCheck.capture());
            gameEndCheck.getValue().run();
            verify(gameStateManager).checkForGameEnd();
        }

        @Test
        @DisplayName("updateCurvesAndDraw draws player curve for alive player without collision")
        void updateCurvesAndDrawDrawsCurveWhenNoCollision() {
            setNoGapCurvesForAllPlayers();
            ReflectionTestUtils.setField(gameEngine, "collisionManager", collisionManager);
            ReflectionTestUtils.setField(gameEngine, "gameRenderer", gameRenderer);
            when(collisionManager.isCollisionDetected(any())).thenReturn(false);

            ReflectionTestUtils.invokeMethod(gameEngine, "updateCurvesAndDraw");

            verify(gameRenderer, atLeastOnce()).drawPlayerCurve(any(), any(), any());
        }

        @Test
        @DisplayName("updateCurvesAndDraw handles collisions when collision manager reports hit")
        void updateCurvesAndDrawHandlesCollisionWhenDetected() {
            setNoGapCurvesForAllPlayers();
            ReflectionTestUtils.setField(gameEngine, "collisionManager", collisionManager);
            ReflectionTestUtils.setField(gameEngine, "gameRenderer", gameRenderer);
            when(collisionManager.isCollisionDetected(any())).thenReturn(true);

            ReflectionTestUtils.invokeMethod(gameEngine, "updateCurvesAndDraw");

            verify(collisionManager, atLeastOnce()).handleCollision(anyInt());
            verify(gameRenderer, atLeastOnce()).drawScorePanel(any(Graphics.class), any(), any());
        }

        @Test
        @DisplayName("updatePlayerMovements applies left and right turning based on input flags")
        void updatePlayerMovementsAppliesTurningFromInputFlags() {
            List<Player> players = gameEngine.getPlayers();
            Player leftPlayer = players.get(0);
            Player rightPlayer = players.get(1);
            Curve leftCurve = new Curve(100, 100, 0, Long.MAX_VALUE);
            Curve rightCurve = new Curve(200, 200, 0, Long.MAX_VALUE);
            leftPlayer.setCurve(leftCurve);
            rightPlayer.setCurve(rightCurve);
            leftPlayer.setLeftKeyPressed(true);
            rightPlayer.setRightKeyPressed(true);

            ReflectionTestUtils.invokeMethod(gameEngine, "updatePlayerMovements");

            assertThat(leftCurve.getDirectionAngle()).isEqualTo(10.0);
            assertThat(rightCurve.getDirectionAngle()).isEqualTo(350.0);
        }

        @Test
        @DisplayName("updateCurvesAndDraw disposes graphics context")
        void updateCurvesAndDrawDisposesGraphicsContext() {
            setNoGapCurvesForAllPlayers();
            ReflectionTestUtils.setField(gameEngine, "collisionManager", collisionManager);
            ReflectionTestUtils.setField(gameEngine, "gameRenderer", gameRenderer);
            when(collisionManager.isCollisionDetected(any())).thenReturn(false);

            Graphics2D g2 = mock(Graphics2D.class);
            BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB) {
                @Override
                public Graphics2D createGraphics() {
                    return g2;
                }
            };
            ReflectionTestUtils.setField(gameEngine, "gameFieldImage", image);

            ReflectionTestUtils.invokeMethod(gameEngine, "updateCurvesAndDraw");

            verify(g2).dispose();
        }

        private void setNoGapCurvesForAllPlayers() {
            List<Player> players = gameEngine.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setCurve(new Curve(100 + (i * 50), 100, 0, Long.MAX_VALUE));
                players.get(i).setAlive(true);
            }
        }
    }
}
