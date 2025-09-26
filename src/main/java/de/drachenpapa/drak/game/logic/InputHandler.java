package de.drachenpapa.drak.game.logic;

import lombok.RequiredArgsConstructor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Handles keyboard input for the game.
 * Updates player input state and allows quitting the game.
 */
@RequiredArgsConstructor
public class InputHandler extends KeyAdapter {

    private final GameEngine gameEngine;

    @Override
    public void keyPressed(KeyEvent e) {
        char pressedKeyChar = e.getKeyChar();
        int pressedKeyCode = e.getKeyCode();
        for (Player player : gameEngine.getPlayers()) {
            if (player.getLeftKey() == pressedKeyChar) {
                player.setLeftKeyPressed(true);
            } else if (player.getRightKey() == pressedKeyChar) {
                player.setRightKeyPressed(true);
            }
        }
        if (pressedKeyCode == KeyEvent.VK_ESCAPE) {
            gameEngine.quitGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char releasedKey = e.getKeyChar();
        for (Player player : gameEngine.getPlayers()) {
            if (player.getLeftKey() == releasedKey) {
                player.setLeftKeyPressed(false);
            } else if (player.getRightKey() == releasedKey) {
                player.setRightKeyPressed(false);
            }
        }
    }
}
