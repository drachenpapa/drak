package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InputHandlerTest {

    private final GameEngine gameEngine = mock(GameEngine.class);

    private InputHandler inputHandler;
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("Player 1", Color.RED, '1', 'q');

        inputHandler = new InputHandler(gameEngine);

        when(gameEngine.getPlayers()).thenReturn(List.of(player));
    }

    @Test
    public void testKeyPressedLeftKey() {
        KeyEvent leftKeyEvent = new KeyEvent(new java.awt.Canvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, '1');
        inputHandler.keyPressed(leftKeyEvent);

        assertThat("The left key should be pressed", player.isLeftKeyPressed(), is(true));
        assertThat("The right key should not be pressed", player.isRightKeyPressed(), is(false));
    }

    @Test
    public void testKeyPressedRightKey() {
        KeyEvent rightKeyEvent = new KeyEvent(new java.awt.Canvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_D, 'q');
        inputHandler.keyPressed(rightKeyEvent);

        assertThat("The right key should be pressed", player.isRightKeyPressed(), is(true));
        assertThat("The left key should not be pressed", player.isLeftKeyPressed(), is(false));
    }

    @Test
    public void testKeyPressedEscapeKey() {
        KeyEvent escapeKeyEvent = new KeyEvent(new java.awt.Canvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
        inputHandler.keyPressed(escapeKeyEvent);

        verify(gameEngine).quitGame();
    }

    @Test
    public void testKeyReleasedLeftKey() {
        player.setLeftKeyPressed(true);
        KeyEvent leftKeyReleaseEvent = new KeyEvent(new java.awt.Canvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_A, '1');
        inputHandler.keyReleased(leftKeyReleaseEvent);

        assertThat("The left key should not be pressed after release", player.isLeftKeyPressed(), is(false));
    }

    @Test
    public void testKeyReleasedRightKey() {
        player.setRightKeyPressed(true);
        KeyEvent rightKeyReleaseEvent = new KeyEvent(new java.awt.Canvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_D, 'q');
        inputHandler.keyReleased(rightKeyReleaseEvent);

        assertThat("The right key should not be pressed after release", player.isRightKeyPressed(), is(false));
    }
}
