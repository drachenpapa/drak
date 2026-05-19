package de.drachenpapa.drak;

import de.drachenpapa.drak.settings.SettingsUI;

import javax.swing.*;

/**
 * Main entry point for the game application.
 * Initializes and starts the game UI on the Event Dispatch Thread.
 */
public class Drak {

    public static final String GAME_TITLE = "Drak";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SettingsUI::new);
    }
}
