package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.view.GamePanel;
import de.drachenpapa.drak.game.view.GameRenderer;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;
import de.drachenpapa.drak.game.view.DefaultGameWindowFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Game engine which handles game logic and lifecycle management.
 * Connects all core components, manages the game loop and state transitions.
 * Coordinates player actions, collision handling, and rendering.
 */
public class GameEngine {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int PLAY_AREA_WIDTH = 680;
    public static final int PLAY_AREA_HEIGHT = WINDOW_HEIGHT;

    private final BufferedImage gameFieldImage;
    private final CollisionManager collisionManager;
    private final GamePanel gamePanel;
    private final GameRenderer gameRenderer;
    private final GameStateManager gameStateManager;
    private final GameWindow gameWindow;
    private final PlayerManager playerManager;
    private final int gameSpeed;

    public GameEngine(List<Player> players, int speed) {
        this(players, speed, new DefaultGameWindowFactory());
    }

    GameEngine(List<Player> players, int speed, GameWindowFactory windowFactory) {
        this.playerManager = new PlayerManager(players);
        this.collisionManager = new CollisionManager(playerManager.getPlayers(), playerManager.getCurvePoints(), playerManager);
        this.gameStateManager = new GameStateManager(playerManager, (players.size() - 1) * 10);
        this.gameRenderer = new GameRenderer();
        this.gameSpeed = 10 * (6 - speed);
        this.gameFieldImage = new BufferedImage(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.gamePanel = new GamePanel(gameRenderer, playerManager, gameStateManager, this, gameFieldImage);
        this.gameWindow = windowFactory.create(gamePanel, this);
    }

    public void startGame() {
        gameStateManager.setGameState(GameState.RUNNING);
        javax.swing.Timer timer = new javax.swing.Timer(gameSpeed, e -> {
            GameState state = gameStateManager.getGameState();
            if (state == GameState.RUNNING || state == GameState.GAME_OVER) {
                updateGameState();
                gamePanel.repaint();
            }
        });
        timer.start();
    }

    public void handleRoundTransition() {
        gameStateManager.handleRoundTransition(this::resetGameForNextRound);
        gamePanel.repaint();
    }

    List<Player> getPlayers() {
        return playerManager.getPlayers();
    }

    void quitGame() {
        gameStateManager.setGameState(GameState.GAME_OVER);
        gameWindow.close();
    }

    private void updateGameState() {
        if (gameStateManager.getGameState() == GameState.GAME_OVER) {
            return;
        }
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
                    handleCollision(g2, i);
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

    private void checkForGameEnd() {
        gameStateManager.checkForGameEnd();
    }

    private void handleCollision(Graphics g, int playerIndex) {
        collisionManager.handleCollision(playerIndex);
        gameRenderer.drawScorePanel(g, playerManager.getPlayers(), this::checkForGameEnd);
    }
}
