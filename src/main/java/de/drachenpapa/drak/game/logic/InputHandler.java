package de.drachenpapa.drak.game.logic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Handles keyboard input for the game using Swing Key Bindings.
 * Key Bindings work regardless of which component currently has focus,
 * avoiding the focus-sensitivity pitfall of raw {@code KeyListener}.
 */
public class InputHandler {

    private final GameEngine gameEngine;

    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = Objects.requireNonNull(gameEngine, "gameEngine");
    }

    private static void bindKey(InputMap inputMap, ActionMap actionMap,
                                char keyChar,
                                String pressActionKey, String releaseActionKey,
                                Runnable onPress, Runnable onRelease) {
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(keyChar);
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, false), pressActionKey);
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), releaseActionKey);
        actionMap.put(pressActionKey, action(e -> onPress.run()));
        actionMap.put(releaseActionKey, action(e -> onRelease.run()));
    }

    private static Action action(Consumer<ActionEvent> handler) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.accept(e);
            }
        };
    }

    public void registerKeyBindings(JComponent component) {
        var inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = component.getActionMap();

        var players = gameEngine.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            var player = players.get(i);
            bindKey(inputMap, actionMap,
                player.getLeftKey(),
                "press_left_" + i,
                "release_left_" + i,
                () -> player.setLeftKeyPressed(true),
                () -> player.setLeftKeyPressed(false));

            bindKey(inputMap, actionMap,
                player.getRightKey(),
                "press_right_" + i,
                "release_right_" + i,
                () -> player.setRightKeyPressed(true),
                () -> player.setRightKeyPressed(false));
        }

        var escStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        inputMap.put(escStroke, "action.quit");
        actionMap.put("action.quit", action(e -> gameEngine.quitGame()));
    }
}
