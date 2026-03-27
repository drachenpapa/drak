package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InputHandler")
@ExtendWith(MockitoExtension.class)
class InputHandlerTest {

    @Mock
    private GameEngine gameEngine;

    private InputHandler inputHandler;
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Player 1", Color.RED, '1', 'q');
        inputHandler = new InputHandler(gameEngine);
        when(gameEngine.getPlayers()).thenReturn(List.of(player));
    }

    @Nested
    @DisplayName("keyPressed()")
    class KeyPressed {

        @Test
        @DisplayName("registers left key for matching player")
        void registersLeftKey() {
            KeyEvent event = new KeyEvent(new Canvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, '1');
            inputHandler.keyPressed(event);

            assertThat(player.isLeftKeyPressed()).isTrue();
            assertThat(player.isRightKeyPressed()).isFalse();
        }

        @Test
        @DisplayName("registers right key for matching player")
        void registersRightKey() {
            KeyEvent event = new KeyEvent(new Canvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_D, 'q');
            inputHandler.keyPressed(event);

            assertThat(player.isRightKeyPressed()).isTrue();
            assertThat(player.isLeftKeyPressed()).isFalse();
        }

        @Test
        @DisplayName("quits game on Escape")
        void quitsGameOnEscape() {
            KeyEvent event = new KeyEvent(new Canvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
            inputHandler.keyPressed(event);

            verify(gameEngine).quitGame();
        }
    }

    @Nested
    @DisplayName("keyReleased()")
    class KeyReleased {

        @Test
        @DisplayName("clears left key for matching player")
        void clearsLeftKey() {
            player.setLeftKeyPressed(true);
            KeyEvent event = new KeyEvent(new Canvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_A, '1');
            inputHandler.keyReleased(event);

            assertThat(player.isLeftKeyPressed()).isFalse();
        }

        @Test
        @DisplayName("clears right key for matching player")
        void clearsRightKey() {
            player.setRightKeyPressed(true);
            KeyEvent event = new KeyEvent(new Canvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_D, 'q');
            inputHandler.keyReleased(event);

            assertThat(player.isRightKeyPressed()).isFalse();
        }
    }
}
