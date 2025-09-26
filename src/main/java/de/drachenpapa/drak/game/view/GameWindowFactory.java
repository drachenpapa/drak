package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.game.logic.GameEngine;

/**
 * Factory interface for creating GameWindow instances.
 * Allows for flexible window creation, e.g. for tests or custom UI variants.
 */
public interface GameWindowFactory {
    GameWindow create(GamePanel panel, GameEngine engine);
}
