package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static de.drachenpapa.drak.Drak.GAME_TITLE;

/**
 * Manages the main game window and its configuration.
 * Sets up the JFrame, content panel, and input handling.
 */
public class GameWindow {

    private final JFrame frame;

    GameWindow(GamePanel gamePanel, GameEngine gameEngine) {
        frame = new JFrame(GAME_TITLE);
        try {
            Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png"));
            frame.setIconImage(logo);
        } catch (Exception ignored) { }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(gamePanel);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addKeyListener(new InputHandler(gameEngine));
        BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        gamePanel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), ""));
    }

    public void close() {
        frame.dispose();
    }
}
