package de.drachenpapa.drak;

import de.drachenpapa.drak.settings.SettingsUI;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the Drak game application.
 * Initializes and starts the game UI.
 */
public class Drak {

    public static final String GAME_TITLE = "Drak";

    public static void main(String[] args) {
        new SettingsUI();
    }

    /**
     * Loads the application icon from the classpath and applies it to the given frame.
     * The icon is optional; the game runs normally if loading fails.
     */
    public static void applyAppIcon(JFrame frame) {
        try {
            Image logo = Toolkit.getDefaultToolkit().getImage(Drak.class.getResource("/logo.png"));
            frame.setIconImage(logo);
        } catch (Exception ignored) {
            // icon is optional; game is fully functional without it
        }
    }
}
