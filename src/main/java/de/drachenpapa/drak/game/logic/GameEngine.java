package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.BalanceSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;
import de.drachenpapa.drak.game.view.DefaultGameWindowFactory;
import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameRenderer;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;

import javax.swing.*;
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
    private final PlayerManager playerManager;
    private final int gameSpeed;
    private GameWindow gameWindow;
    private Timer gameTimer;

    private GameEngine(int gameSpeed, PlayerManager playerManager, CollisionManager collisionManager,
                       GameStateManager gameStateManager, GameRenderer gameRenderer,
                       BufferedImage gameFieldImage, GamePanel gamePanel) {
        this.gameSpeed = gameSpeed;
        this.playerManager = playerManager;
        this.collisionManager = collisionManager;
        this.gameStateManager = gameStateManager;
        this.gameRenderer = gameRenderer;
        this.gameFieldImage = gameFieldImage;
        this.gamePanel = gamePanel;
    }

    public static GameEngine create(GameConfig config) {
        return create(config, new DefaultGameWindowFactory());
    }

    static GameEngine create(GameConfig config, GameWindowFactory windowFactory) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(windowFactory, "windowFactory");

        int gameSpeed = calculateGameSpeed(config.speed());
        List<Player> players = config.createPlayers(gameSpeed);
        PlayerManager pm = new PlayerManager(players);
        CollisionManager cm = new CollisionManager(pm);
        GameStateManager gsm = new GameStateManager(pm, calculateWinningScore(players.size()));
        GameRenderer gr = new GameRenderer();
        BufferedImage image = new BufferedImage(
            DisplaySettings.PLAY_AREA_WIDTH, DisplaySettings.PLAY_AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        GamePanel panel = new GamePanel(gr, pm, gsm, image);

        GameEngine engine = new GameEngine(gameSpeed, pm, cm, gsm, gr, image, panel);
        engine.gameWindow = windowFactory.create(panel, engine);
        return engine;
    }

    private static int calculateWinningScore(int playerCount) {
        return (playerCount - 1) * BalanceSettings.POINTS_PER_OPPONENT;
    }

    private static int calculateGameSpeed(int speedLevel) {
        return BalanceSettings.SPEED_DELAY_MULTIPLIER * (BalanceSettings.SPEED_LEVEL_INVERSION_OFFSET - speedLevel);
    }

    public void startGame() {
        Objects.requireNonNull(gameWindow, "gameWindow must be set before calling startGame()");
        gameWindow.show();
        gameStateManager.setGameState(GameState.RUNNING);
        stopGameTimer();
        gameTimer = new Timer(gameSpeed, e -> handleGameTick(gameStateManager.getGameState()));
        gameTimer.start();
    }

    private void handleGameTick(GameState state) {
        switch (state) {
            case RUNNING -> {
                updateGameState();
                gamePanel.repaint();
            }
            case READY_FOR_NEXT_ROUND -> {
                handleRoundTransition();
                gamePanel.repaint();
            }
            case GAME_OVER -> {
                stopGameTimer();
                gamePanel.repaint();
            }
            case STARTED, PAUSED -> {
                // No action for these states
            }
        }
    }

    void handleRoundTransition() {
        gameStateManager.handleRoundTransition(this::resetGameForNextRound);
    }

    List<Player> getPlayers() {
        return playerManager.getPlayers();
    }

    public void quitGame() {
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
        tickPlayerGaps();
        detectCollisions();
        drawCurveTrails();
        updatePlayerMovements();
    }

    private void tickPlayerGaps() {
        for (var player : playerManager.getPlayers()) {
            if (player.isAlive()) {
                player.getCurve().tickGap();
            }
        }
    }

    private void detectCollisions() {
        var players = playerManager.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            var player = players.get(i);
            var curve = player.getCurve();
            if (player.isAlive() && !curve.isGapActive()) {
                collisionManager.wrapCurvePosition(curve);
                if (collisionManager.isCollisionDetected(curve, i)) {
                    handleCollision(i);
                }
            }
        }
    }

    private void drawCurveTrails() {
        var g2 = gameFieldImage.createGraphics();
        try {
            for (var player : playerManager.getPlayers()) {
                var curve = player.getCurve();
                if (player.isAlive() && !curve.isGapActive()) {
                    gameRenderer.drawPlayerCurve(g2, curve, player.getColor());
                }
            }
        } finally {
            g2.dispose();
        }
    }

    private void updatePlayerMovements() {
        for (var player : playerManager.getPlayers()) {
            if (!player.isAlive()) {
                continue;
            }
            var curve = player.getCurve();
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

    private void handleCollision(int playerIndex) {
        collisionManager.handleCollision(playerIndex);
        gameStateManager.checkForGameEnd();
    }
}
