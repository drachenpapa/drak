package de.drachenpapa.drak.game.config;

/**
 * Display and window dimension constants.
 */
public final class DisplaySettings {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int PLAY_AREA_WIDTH = 680;
    public static final int PLAY_AREA_HEIGHT = WINDOW_HEIGHT;
    public static final int SCORE_PANEL_X = PLAY_AREA_WIDTH + 2;
    public static final int SCORE_PANEL_WIDTH = WINDOW_WIDTH - SCORE_PANEL_X;

    private DisplaySettings() {
    }

    public static boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= PLAY_AREA_WIDTH || y < 0 || y >= PLAY_AREA_HEIGHT;
    }
}
