package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.game.config.DisplaySettings;
import de.drachenpapa.drak.game.config.RenderSettings;
import de.drachenpapa.drak.game.logic.GameStateManager;
import de.drachenpapa.drak.game.logic.PlayerManager;

import javax.swing.*;
import java.awt.*;

/**
 * Main panel for rendering the game field and UI elements.
 * Delegates all drawing operations to the GameRenderer.
 */
public class GamePanel extends JPanel {

    private final transient GameRenderer gameRenderer;
    private final transient PlayerManager playerManager;
    private final transient GameStateManager gameStateManager;
    private final transient Image gameFieldImage;

    public GamePanel(GameRenderer gameRenderer, PlayerManager playerManager, GameStateManager gameStateManager, Image gameFieldImage) {
        this.gameRenderer = gameRenderer;
        this.playerManager = playerManager;
        this.gameStateManager = gameStateManager;
        this.gameFieldImage = gameFieldImage;
        setPreferredSize(new Dimension(DisplaySettings.WINDOW_WIDTH, DisplaySettings.WINDOW_HEIGHT));
        setBackground(RenderSettings.GAME_FIELD_BACKGROUND);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameRenderer.drawGame(
            g,
            gameFieldImage,
            playerManager.getPlayers(),
            gameStateManager.getGameState()
        );
    }
}
