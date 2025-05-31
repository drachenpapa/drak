package de.drachenpapa.zatacka.game.view;

import de.drachenpapa.zatacka.game.logic.GameEngine;

/**
 * Default factory for creating GameWindow instances.
 * Used in production code to encapsulate the standard window logic.
 */
public class DefaultGameWindowFactory implements GameWindowFactory {
    @Override
    public GameWindow create(GamePanel panel, GameEngine engine) {
        return new GameWindow(panel, engine);
    }
}
