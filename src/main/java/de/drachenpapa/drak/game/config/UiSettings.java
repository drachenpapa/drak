package de.drachenpapa.drak.game.config;

/**
 * UI layout coordinates, font sizes, and text constants.
 */
public final class UiSettings {

    public static final int SCORE_NAME_X = 690;
    public static final int SCORE_NAME_BASE_Y = 30;
    public static final int SCORE_POINTS_BASE_Y = 50;
    public static final int SCORE_POINTS_OFFSET_Y = SCORE_POINTS_BASE_Y - SCORE_NAME_BASE_Y;
    public static final int SCORE_ROW_OFFSET_Y = 50;
    public static final int SCORE_FONT_SIZE = 16;
    public static final int FINAL_TITLE_X = 200;
    public static final int FINAL_TITLE_Y = 100;
    public static final int FINAL_ROW_NAME_X = 200;
    public static final int FINAL_ROW_POINTS_X = 500;
    public static final int FINAL_ROW_BASE_Y = 175;
    public static final int FINAL_ROW_OFFSET_Y = 75;
    public static final int FINAL_TITLE_FONT_SIZE = 72;
    public static final int FINAL_ROW_FONT_SIZE = 36;
    public static final String FINAL_SCORES_TITLE = "Final Scores";
    public static final String SCORE_SUFFIX = " pts";

    private UiSettings() {
    }
}
