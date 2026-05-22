package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.TestReflectionUtils;
import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameRenderer;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        GameConfig config = new GameConfig(3, List.of(
            new PlayerConfig("Player 1", Color.RED, '1', 'q'),
            new PlayerConfig("Player 2", Color.GREEN, 'y', 'x')
        ));
        gameEngine = GameEngine.create(config, gameWindowFactory);

        TestReflectionUtils.setField(gameEngine, "gameStateManager", gameStateManager);
        TestReflectionUtils.setField(gameEngine, "gamePanel", gamePanel);
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

            GameEngine engine = GameEngine.create(config, gameWindowFactory);

            verify(gameWindowFactory).create(any(), any());
            assertThat(TestReflectionUtils.getField(engine, "gameSpeed")).isEqualTo(30);
            GameStateManager stateManager = (GameStateManager) TestReflectionUtils.getField(engine, "gameStateManager");
            assertThat(stateManager).isNotNull();
            assertThat(TestReflectionUtils.getField(stateManager, "winningScore")).isEqualTo(10);
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

            GameEngine engine = GameEngine.create(config, gameWindowFactory);

            GameStateManager stateManager = (GameStateManager) TestReflectionUtils.getField(engine, "gameStateManager");
            assertThat(stateManager).isNotNull();
            assertThat(TestReflectionUtils.getField(stateManager, "winningScore")).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("handleRoundTransition()")
    class HandleRoundTransition {

        @Test
        @DisplayName("delegates to GameStateManager; repaint is the caller's responsibility")
        void delegatesToGameStateManager() {
            gameEngine.handleRoundTransition();
            verify(gameStateManager).handleRoundTransition(any());
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
            TestReflectionUtils.setField(gameEngine, "gameTimer", timer);

            gameEngine.quitGame();

            verify(timer).stop();
        }
    }

    @Nested
    @DisplayName("startGame()")
    class StartGame {

        @AfterEach
        void stopRunningTimerAfterTest() {
            Timer timer = (Timer) TestReflectionUtils.getField(gameEngine, "gameTimer");
            if (timer != null) {
                timer.stop();
            }
        }

        @Test
        @DisplayName("sets game state to RUNNING")
        void setsGameStateToRunning() {
            gameEngine.startGame();
            verify(gameStateManager).setGameState(GameState.RUNNING);
        }

        @Test
        @DisplayName("shows game window on start")
        void showsGameWindow() {
            gameEngine.startGame();
            verify(gameWindow).show();
        }

        @Test
        @DisplayName("stops existing timer before creating a new one")
        void stopsExistingTimerBeforeCreatingNewOne() {
            Timer oldTimer = mock(Timer.class);
            TestReflectionUtils.setField(gameEngine, "gameTimer", oldTimer);

            gameEngine.startGame();

            verify(oldTimer).stop();
        }

        @Test
        @DisplayName("starts created timer")
        void startsCreatedTimer() {
            gameEngine.startGame();

            Timer timer = (Timer) TestReflectionUtils.getField(gameEngine, "gameTimer");
            assertThat(timer).isNotNull();
            assertThat(timer.isRunning()).isTrue();
            timer.stop();
        }

        @Test
        @DisplayName("repaints panel when timer tick runs in RUNNING state")
        void repaintsPanelOnTimerTickInRunningState() {
            when(gameStateManager.getGameState()).thenReturn(GameState.RUNNING);
            Player firstPlayer = gameEngine.getPlayers().getFirst();
            int beforeX = firstPlayer.getCurve().getXPosition();
            int beforeY = firstPlayer.getCurve().getYPosition();

            gameEngine.startGame();
            Timer timer = (Timer) TestReflectionUtils.getField(gameEngine, "gameTimer");
            assertThat(timer).isNotNull();
            timer.stop();
            clearInvocations(gamePanel);

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
            Timer timer = (Timer) TestReflectionUtils.getField(gameEngine, "gameTimer");
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
            Player firstPlayer = gameEngine.getPlayers().getFirst();
            int beforePointCount = firstPlayer.getCurve().getPoints().size();

            TestReflectionUtils.invokeMethod(gameEngine, "updateGameState");

            assertThat(firstPlayer.getCurve().getPoints()).hasSize(beforePointCount + 1);
        }

        @Test
        @DisplayName("updateGameState updates game field occupancy when running")
        void updateGameStateUpdatesGameFieldOccupancyWhenRunning() {
            PlayerManager manager = (PlayerManager) TestReflectionUtils.getField(gameEngine, "playerManager");
            assertThat(manager).isNotNull();

            int beforeOccupied = countOccupiedCells(manager.getOccupiedGrid());

            TestReflectionUtils.invokeMethod(gameEngine, "updateGameState");

            int afterOccupied = countOccupiedCells(manager.getOccupiedGrid());
            assertThat(afterOccupied).isGreaterThan(beforeOccupied);
        }

        @Test
        @DisplayName("updateGameState ticks gaps for alive players")
        void updateGameStateTicksGapsForAlivePlayers() {
            Player player = gameEngine.getPlayers().getFirst();
            Curve gapCurve = new Curve(100, 100, 0, 1);
            TestReflectionUtils.setField(gapCurve, "ticksSinceLastGap", 1);
            player.setCurve(gapCurve);
            player.revive();

            TestReflectionUtils.invokeMethod(gameEngine, "updateGameState");

            assertThat(gapCurve.isGapActive()).isTrue();
        }

        @Test
        @DisplayName("updateGameState always processes players; GAME_OVER guard lives in handleGameTick")
        void updateGameStateAlwaysProcessesPlayers() {
            Player firstPlayer = gameEngine.getPlayers().getFirst();
            int beforePointCount = firstPlayer.getCurve().getPoints().size();

            TestReflectionUtils.invokeMethod(gameEngine, "updateGameState");

            assertThat(firstPlayer.getCurve().getPoints()).hasSize(beforePointCount + 1);
        }

        @Test
        @DisplayName("handleCollision delegates to collision manager and game-end check")
        void handleCollisionDelegatesToCollisionAndGameEndCheck() {
            TestReflectionUtils.setField(gameEngine, "collisionManager", collisionManager);

            TestReflectionUtils.invokeMethod(gameEngine, "handleCollision", 0);

            verify(collisionManager).handleCollision(0);
            verify(gameStateManager).checkForGameEnd();
        }

        @Test
        @DisplayName("drawCurveTrails draws player curve for alive player without gap")
        void drawCurveTrailsDrawsCurveForAlivePlayer() {
            setNoGapCurvesForAllPlayers();
            TestReflectionUtils.setField(gameEngine, "gameRenderer", gameRenderer);

            TestReflectionUtils.invokeMethod(gameEngine, "drawCurveTrails");

            verify(gameRenderer, atLeastOnce()).drawPlayerCurve(any(), any(), any());
        }

        @Test
        @DisplayName("detectCollisions handles collisions when collision manager reports hit")
        void detectCollisionsHandlesCollisionWhenDetected() {
            setNoGapCurvesForAllPlayers();
            TestReflectionUtils.setField(gameEngine, "collisionManager", collisionManager);
            when(collisionManager.isCollisionDetected(any(), anyInt())).thenReturn(true);

            TestReflectionUtils.invokeMethod(gameEngine, "detectCollisions");

            verify(collisionManager, atLeastOnce()).handleCollision(anyInt());
        }

        @Test
        @DisplayName("updatePlayerMovements applies left and right turning based on input flags")
        void updatePlayerMovementsAppliesTurningFromInputFlags() {
            List<Player> players = gameEngine.getPlayers();
            Player leftPlayer = players.get(0);
            Player rightPlayer = players.get(1);
            Curve leftCurve = new Curve(100, 100, 0, Integer.MAX_VALUE);
            Curve rightCurve = new Curve(200, 200, 0, Integer.MAX_VALUE);
            leftPlayer.setCurve(leftCurve);
            rightPlayer.setCurve(rightCurve);
            leftPlayer.setLeftKeyPressed(true);
            rightPlayer.setRightKeyPressed(true);

            TestReflectionUtils.invokeMethod(gameEngine, "updatePlayerMovements");

            assertThat(leftCurve.getDirectionAngle()).isEqualTo(10.0);
            assertThat(rightCurve.getDirectionAngle()).isEqualTo(350.0);
        }

        @Test
        @DisplayName("drawCurveTrails disposes graphics context")
        void drawCurveTrailsDisposesGraphicsContext() {
            setNoGapCurvesForAllPlayers();
            TestReflectionUtils.setField(gameEngine, "gameRenderer", gameRenderer);

            Graphics2D g2 = mock(Graphics2D.class);
            BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB) {
                @Override
                public Graphics2D createGraphics() {
                    return g2;
                }
            };
            TestReflectionUtils.setField(gameEngine, "gameFieldImage", image);

            TestReflectionUtils.invokeMethod(gameEngine, "drawCurveTrails");

            verify(g2).dispose();
        }

        private void setNoGapCurvesForAllPlayers() {
            List<Player> players = gameEngine.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setCurve(new Curve(100 + (i * 50), 100, 0, Integer.MAX_VALUE));
                players.get(i).revive();
            }
        }

        private int countOccupiedCells(int[][] occupiedGrid) {
            int occupied = 0;
            for (int[] row : occupiedGrid) {
                for (int owner : row) {
                    if (owner != 0) {
                        occupied++;
                    }
                }
            }
            return occupied;
        }
    }
}
