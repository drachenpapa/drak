package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.SwingUtils;
import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.InputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;


import static de.drachenpapa.drak.AppConstants.GAME_TITLE;

/**
 * Manages the main game window and its configuration.
 * Sets up the JFrame, content panel, and input handling.
 */
public class GameWindow {

    private static final Logger logger = LoggerFactory.getLogger(GameWindow.class);

    private final JFrame frame;

    GameWindow(GamePanel gamePanel, GameEngine gameEngine) {
        frame = new JFrame(GAME_TITLE);
        initializeWindowIcon();
        configureFrame(gamePanel, gameEngine);
        hideCursor(gamePanel);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void close() {
        frame.dispose();
    }

    private void initializeWindowIcon() {
        SwingUtils.applyWindowIcon(frame, "/logo.png", logger);
    }

    private void configureFrame(GamePanel gamePanel, GameEngine gameEngine) {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameEngine.quitGame();
            }
        });
        frame.setContentPane(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        new InputHandler(gameEngine).registerKeyBindings(gamePanel);
    }

    private void hideCursor(GamePanel gamePanel) {
        var cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        gamePanel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), ""));
    }
}
