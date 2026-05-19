package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.BalanceSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;
import de.drachenpapa.drak.game.view.DefaultGameWindowFactory;
import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameRenderer;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * Game engine which handles game logic and lifecycle management.
 * Connects all core components, manages the game loop and state transitions.
 * Coordinates player actions, collision handling, and rendering.
 */
public class GameEngine {

    private final BufferedImage gameFieldImage;
    private final CollisionManager collisionManager;
    private final GamePanel gamePanel;
    private final GameRenderer gameRenderer;
    private final GameStateManager gameStateManager;
    private final GameWindow gameWindow;
    private final PlayerManager playerManager;
    private final int gameSpeed;
    private Timer gameTimer;

    public GameEngine(GameConfig config) {
        this(config, new DefaultGameWindowFactory());
    }

    GameEngine(GameConfig config, GameWindowFactory windowFactory) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(windowFactory, "windowFactory");

        EngineSetup setup = EngineSetup.from(config);
        this.playerManager = setup.playerManager();
        this.collisionManager = setup.collisionManager();
        this.gameStateManager = setup.gameStateManager();
        this.gameRenderer = setup.gameRenderer();
        this.gameSpeed = setup.gameSpeed();
        this.gameFieldImage = setup.gameFieldImage();
        this.gamePanel = new GamePanel(gameRenderer, playerManager, gameStateManager, gameFieldImage);
        this.gameWindow = windowFactory.create(gamePanel, this);
    }


    public void startGame() {
        gameStateManager.setGameState(GameState.RUNNING);
        stopGameTimer();
        gameTimer = new Timer(gameSpeed, e -> {
            switch (gameStateManager.getGameState()) {
                case RUNNING -> {
                    updateGameState();
                    gamePanel.repaint();
                }
                case READY_FOR_NEXT_ROUND -> {
                    handleRoundTransition();
                    gamePanel.repaint();
                }
                case GAME_OVER -> gamePanel.repaint();
                default -> { /* STARTED, PAUSED – no action */ }
            }
        });
        gameTimer.start();
    }

    public void handleRoundTransition() {
        gameStateManager.handleRoundTransition(this::resetGameForNextRound);
        gamePanel.repaint();
    }

    List<Player> getPlayers() {
        return playerManager.getPlayers();
    }

    void quitGame() {
        stopGameTimer();
        gameStateManager.stopTimers();
        gameStateManager.setGameState(GameState.GAME_OVER);
        gameWindow.close();
    }

    private void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }

    private void updateGameState() {
        if (gameStateManager.getGameState() == GameState.GAME_OVER) {
            return;
        }
        tickPlayerGaps();
        updateCurvesAndDraw();
        updatePlayerMovements();
    }

    private void tickPlayerGaps() {
        for (Player player : playerManager.getPlayers()) {
            if (player.isAlive()) {
                player.getCurve().tickGap();
            }
        }
    }

    private void updateCurvesAndDraw() {
        Graphics2D g2 = gameFieldImage.createGraphics();
        try {
            List<Player> players = playerManager.getPlayers();

            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                Curve curve = player.getCurve();

                if (player.isAlive() && !curve.isGapActive()) {
                    if (collisionManager.isCollisionDetected(curve, i)) {
                        handleCollision(g2, i);
                    } else {
                        gameRenderer.drawPlayerCurve(g2, curve, player.getColor());
                    }
                }
            }
        } finally {
            g2.dispose();
        }
    }

    private void updatePlayerMovements() {
        for (Player player : playerManager.getPlayers()) {
            Curve curve = player.getCurve();
            curve.move();

            if (player.isLeftKeyPressed()) {
                curve.turnLeft();
            } else if (player.isRightKeyPressed()) {
                curve.turnRight();
            }
        }
    }

    private void resetGameForNextRound() {
        playerManager.resetForNextRound();
        gameRenderer.clearGameField(gameFieldImage);
    }

    private void checkForGameEnd() {
        gameStateManager.checkForGameEnd();
    }

    private void handleCollision(Graphics g, int playerIndex) {
        collisionManager.handleCollision(playerIndex);
        gameRenderer.drawScorePanel(g, playerManager.getPlayers(), this::checkForGameEnd);
    }

    private record EngineSetup(PlayerManager playerManager,
                               CollisionManager collisionManager,
                               GameStateManager gameStateManager,
                               GameRenderer gameRenderer,
                               int gameSpeed,
                               BufferedImage gameFieldImage) {

        private static int calculateWinningScore(int playerCount) {
            return (playerCount - 1) * BalanceSettings.POINTS_PER_OPPONENT;
        }

        private static int calculateGameSpeed(int speedLevel) {
            return BalanceSettings.SPEED_DELAY_MULTIPLIER * (BalanceSettings.SPEED_LEVEL_INVERSION_OFFSET - speedLevel);
        }

        private static EngineSetup from(GameConfig config) {
            List<Player> players = config.createPlayers();
            PlayerManager playerManager = new PlayerManager(players);
            CollisionManager collisionManager = new CollisionManager(playerManager);
            GameStateManager gameStateManager = new GameStateManager(playerManager, calculateWinningScore(players.size()));
            GameRenderer gameRenderer = new GameRenderer();
            int gameSpeed = calculateGameSpeed(config.speed());
            BufferedImage gameFieldImage = new BufferedImage(DisplaySettings.PLAY_AREA_WIDTH, DisplaySettings.PLAY_AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            return new EngineSetup(playerManager, collisionManager, gameStateManager, gameRenderer, gameSpeed, gameFieldImage);
        }
    }
}
