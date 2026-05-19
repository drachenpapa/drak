package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.drachenpapa.drak.Drak.GAME_TITLE;

/**
 * Manages the main game window and its configuration.
 * Sets up the JFrame, content panel, and input handling.
 */
public class GameWindow {

    private static final Logger logger = Logger.getLogger(GameWindow.class.getName());

    private final JFrame frame;

    GameWindow(GamePanel gamePanel, GameEngine gameEngine) {
        frame = new JFrame(GAME_TITLE);
        initializeWindowIcon();
        configureFrame(gamePanel, gameEngine);
        hideCursor(gamePanel);
    }

    private void initializeWindowIcon() {
        URL logoUrl = getClass().getResource("/logo.png");
        if (logoUrl != null) {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(logoUrl));
        } else {
            logger.log(Level.WARNING, "Logo resource not found [/logo.png]; window icon will not be set");
        }
    }

    private void configureFrame(GamePanel gamePanel, GameEngine gameEngine) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(gamePanel);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addKeyListener(new InputHandler(gameEngine));
    }

    private void hideCursor(GamePanel gamePanel) {
        BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        gamePanel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), ""));
    }

    public void close() {
        frame.dispose();
    }
}
