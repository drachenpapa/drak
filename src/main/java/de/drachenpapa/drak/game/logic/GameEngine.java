package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.view.DefaultGameWindowFactory;
import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameRenderer;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Game engine that handles game logic and lifecycle management.
 * Connects all core components, manages the game loop and state transitions.
 * Coordinates player actions, collision handling, and rendering.
 */
public class GameEngine {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int PLAY_AREA_WIDTH = 680;
    public static final int PLAY_AREA_HEIGHT = WINDOW_HEIGHT;
    public static final int MIN_SPEED_LEVEL = 1;
    public static final int MAX_SPEED_LEVEL = 5;
    public static final int DEFAULT_SPEED_LEVEL = 3;

    private static final int SPEED_INTERVAL_MS = 10;

    static final int WINNING_SCORE_PER_PLAYER = 10;

    private final BufferedImage gameFieldImage;
    private final CollisionManager collisionManager;
    private final GamePanel gamePanel;
    private final GameRenderer gameRenderer;
    private final GameStateManager gameStateManager;
    private final GameWindow gameWindow;
    private final PlayerManager playerManager;
    private final Runnable onGameEnd;
    private final int gameSpeed;

    private Timer gameLoopTimer;

    public GameEngine(List<Player> players, int speed) {
        this(players, speed, new DefaultGameWindowFactory(), () -> { });
    }

    public GameEngine(List<Player> players, int speed, Runnable onGameEnd) {
        this(players, speed, new DefaultGameWindowFactory(), onGameEnd);
    }

    private GameEngine(List<Player> players, int speed, GameWindowFactory windowFactory, Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
        this.playerManager = new PlayerManager(players);
        this.collisionManager = new CollisionManager(playerManager.getPlayers(), playerManager.getCurvePoints(), playerManager);
        this.gameStateManager = new GameStateManager(playerManager, (players.size() - 1) * WINNING_SCORE_PER_PLAYER);
        this.gameRenderer = new GameRenderer();
        this.gameSpeed = SPEED_INTERVAL_MS * (MAX_SPEED_LEVEL + 1 - speed);
        this.gameFieldImage = new BufferedImage(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.gamePanel = new GamePanel(gameRenderer, playerManager, gameStateManager, gameFieldImage);
        this.gameWindow = windowFactory.create(gamePanel, this);
    }

    /**
     * Test-only constructor allowing mock injection of {@code gameStateManager} and {@code gamePanel}.
     */
    GameEngine(List<Player> players, int speed, GameWindowFactory windowFactory, GameStateManager gameStateManager, GamePanel gamePanel, Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
        this.playerManager = new PlayerManager(players);
        this.collisionManager = new CollisionManager(playerManager.getPlayers(), playerManager.getCurvePoints(), playerManager);
        this.gameStateManager = gameStateManager;
        this.gameRenderer = new GameRenderer();
        this.gameSpeed = SPEED_INTERVAL_MS * (MAX_SPEED_LEVEL + 1 - speed);
        this.gameFieldImage = new BufferedImage(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.gamePanel = gamePanel;
        this.gameWindow = windowFactory.create(gamePanel, this);
    }

    public void startGame() {
        gameStateManager.setGameState(GameState.RUNNING);
        gameLoopTimer = new Timer(gameSpeed, e -> {
            if (gameStateManager.getGameState() == GameState.RUNNING) {
                updateGameState();
            }
            handleRoundTransition();
            gamePanel.repaint();
        });
        gameLoopTimer.start();
    }

    void handleRoundTransition() {
        gameStateManager.handleRoundTransition(this::resetGameForNextRound);
    }

    List<Player> getPlayers() {
        return playerManager.getPlayers();
    }

    void quitGame() {
        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
        gameStateManager.setGameState(GameState.GAME_OVER);
        gameWindow.close();
        onGameEnd.run();
    }

    private void updateGameState() {
        updateCurvesAndDraw();
        updatePlayerMovements();
    }

    private void updateCurvesAndDraw() {
        Graphics2D g2 = gameFieldImage.createGraphics();
        List<Player> players = playerManager.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Curve curve = player.getCurve();

            if (player.isAlive() && !curve.isGeneratingGap()) {
                if (collisionManager.isCollisionDetected(curve)) {
                    handleCollision(i);
                } else {
                    gameRenderer.drawPlayerCurve(g2, curve, player.getColor());
                }
            }
        }

        g2.dispose();
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

    private void handleCollision(int playerIndex) {
        collisionManager.handleCollision(playerIndex);
        gameStateManager.checkForGameEnd();
    }
}
