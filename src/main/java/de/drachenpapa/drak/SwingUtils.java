package de.drachenpapa.drak;

import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Utility methods for common Swing operations.
 */
public final class SwingUtils {

    private SwingUtils() {
    }

    public static void applyWindowIcon(JFrame frame, String resource, Logger logger) {
        URL logoUrl = SwingUtils.class.getResource(resource);
        if (logoUrl == null) {
            logger.warn("Logo resource not found [{}]; window icon will not be set", resource);
            return;
        }
        try {
            Image icon = ImageIO.read(logoUrl);
            frame.setIconImage(icon);
        } catch (IOException ex) {
            logger.warn("Failed to load window icon", ex);
        }
    }
}
