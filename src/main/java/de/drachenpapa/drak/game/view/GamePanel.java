package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.GameStateManager;
import de.drachenpapa.drak.game.logic.PlayerManager;

import javax.swing.*;
import java.awt.*;

/**
 * Main panel for rendering the game field and UI elements.
 * Delegates all drawing operations to the GameRenderer.
 */
public class GamePanel extends JPanel {

    private final GameRenderer gameRenderer;
    private final PlayerManager playerManager;
    private final GameStateManager gameStateManager;
    private final GameEngine gameEngine;
    private final Image gameFieldImage;

    public GamePanel(GameRenderer gameRenderer, PlayerManager playerManager, GameStateManager gameStateManager, GameEngine gameEngine, Image gameFieldImage) {
        this.gameRenderer = gameRenderer;
        this.playerManager = playerManager;
        this.gameStateManager = gameStateManager;
        this.gameEngine = gameEngine;
        this.gameFieldImage = gameFieldImage;
        setPreferredSize(new Dimension(GameEngine.WINDOW_WIDTH, GameEngine.WINDOW_HEIGHT));
        setBackground(Color.black);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameRenderer.drawGame(
                g,
                gameFieldImage,
                playerManager.getPlayers(),
                gameStateManager.getGameState(),
                gameEngine::handleRoundTransition
        );
    }
}
