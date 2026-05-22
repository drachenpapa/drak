package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private Player player;
    private JPanel component;

    @BeforeEach
    void setUp() {
        player = new Player("Player 1", Color.RED, '1', 'q');
        InputHandler inputHandler = new InputHandler(gameEngine);
        when(gameEngine.getPlayers()).thenReturn(List.of(player));
        component = new JPanel();
        inputHandler.registerKeyBindings(component);
    }

    private void triggerKey(char keyChar, boolean onRelease) {
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(keyChar);
        KeyStroke ks = KeyStroke.getKeyStroke(keyCode, 0, onRelease);
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        String actionKey = (String) inputMap.get(ks);
        assertThat(actionKey).as("No binding registered for char '%s'", keyChar).isNotNull();
        component.getActionMap().get(actionKey)
            .actionPerformed(new ActionEvent(component, ActionEvent.ACTION_PERFORMED, actionKey));
    }

    @Nested
    @DisplayName("key pressed")
    class KeyPressed {

        @Test
        @DisplayName("registers left key for matching player")
        void registersLeftKey() {
            triggerKey('1', false);

            assertThat(player.isLeftKeyPressed()).isTrue();
            assertThat(player.isRightKeyPressed()).isFalse();
        }

        @Test
        @DisplayName("registers right key for matching player")
        void registersRightKey() {
            triggerKey('q', false);

            assertThat(player.isRightKeyPressed()).isTrue();
            assertThat(player.isLeftKeyPressed()).isFalse();
        }

        @Test
        @DisplayName("quits game on Escape")
        void quitsGameOnEscape() {
            KeyStroke escKs = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
            String actionKey = (String) component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(escKs);
            component.getActionMap().get(actionKey)
                .actionPerformed(new ActionEvent(component, ActionEvent.ACTION_PERFORMED, actionKey));

            verify(gameEngine).quitGame();
        }
    }

    @Nested
    @DisplayName("key released")
    class KeyReleased {

        @Test
        @DisplayName("clears left key for matching player")
        void clearsLeftKey() {
            player.setLeftKeyPressed(true);
            triggerKey('1', true);

            assertThat(player.isLeftKeyPressed()).isFalse();
        }

        @Test
        @DisplayName("clears right key for matching player")
        void clearsRightKey() {
            player.setRightKeyPressed(true);
            triggerKey('q', true);

            assertThat(player.isRightKeyPressed()).isFalse();
        }
    }
}
