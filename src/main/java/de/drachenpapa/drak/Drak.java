package de.drachenpapa.drak;

import de.drachenpapa.drak.settings.SettingsUI;

/**
 * Main entry point for the Drak game application.
 * Initializes and starts the game UI.
 */
public class Drak {

    public static final String GAME_TITLE = "Drak";

    public static void main(String[] args) {
        new SettingsUI();
    }
}
